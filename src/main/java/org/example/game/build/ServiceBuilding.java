package org.example.game.build;

import org.example.game.person.Character;
import org.example.game.person.NPC;
import org.example.game.time.GameTime;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public abstract class ServiceBuilding extends Building {
    protected final int maxVisitors;
    protected final Map<Character, Service> activeServices;
    protected final List<Service> availableServices;
    protected final List<NPC> regularVisitors;
    protected final Queue<ServiceRequest> serviceQueue; // Очередь на услуги
    protected int currentVisitors;
    
    protected void addService(Service service) {
        availableServices.add(service);
    }
    
    protected static class ServiceRequest {
        private final Character visitor;
        private final Service service;
        private final long requestTime;
        private int queuePosition;
        
        public ServiceRequest(Character visitor, Service service) {
            this.visitor = visitor;
            this.service = service;
            this.requestTime = GameTime.getGameTimeMs();
            this.queuePosition = 0;
        }
        
        public Character getVisitor() { return visitor; }
        public Service getService() { return service; }
        public long getRequestTime() { return requestTime; }
        public int getQueuePosition() { return queuePosition; }
        public void setQueuePosition(int position) { this.queuePosition = position; }
    }
    
    protected static class Service {
        private final String name;
        private final int durationMinutes;
        private final int goldCost;
        private final Map<String, Integer> bonuses;
        
        public Service(String name, int durationMinutes, int goldCost, Map<String, Integer> bonuses) {
            this.name = name;
            this.durationMinutes = durationMinutes;
            this.goldCost = goldCost;
            this.bonuses = bonuses;
        }
        
        public String getName() { return name; }
        public int getDurationMinutes() { return durationMinutes; }
        public int getGoldCost() { return goldCost; }
        public Map<String, Integer> getBonuses() { return bonuses; }
    }
    
    public ServiceBuilding(String name, int maxVisitors) {
        super(name, false);
        this.maxVisitors = maxVisitors;
        this.activeServices = new ConcurrentHashMap<>();
        this.availableServices = new ArrayList<>();
        this.regularVisitors = new ArrayList<>();
        this.serviceQueue = new LinkedList<>();
        this.currentVisitors = 0;
    }
    
    public boolean canAcceptVisitor() {
        return currentVisitors < maxVisitors;
    }
    
    public List<Service> getAvailableServices() {
        return Collections.unmodifiableList(availableServices);
    }
    
    public boolean startService(Character visitor, int serviceIndex) {
        if (serviceIndex < 0 || serviceIndex >= availableServices.size()) {
            System.out.println("Неверный индекс услуги: " + serviceIndex);
            return false;
        }
        
        Service service = availableServices.get(serviceIndex);
        
        // Для NPC не проверяем стоимость услуги
        if (!(visitor instanceof NPC)) {
            // Проверяем, может ли посетитель позволить себе услугу
            if (visitor.getGold() < service.getGoldCost()) {
                System.out.println(visitor.getName() + " не может позволить себе услугу " + service.getName() + 
                                 " (стоимость: " + service.getGoldCost() + " золота)");
                return false;
            }
            // Списываем стоимость услуги только для героя
            visitor.setGold(visitor.getGold() - service.getGoldCost());
        }
        
        // Создаем запрос на услугу
        ServiceRequest request = new ServiceRequest(visitor, service);
        
        // Если есть свободное место - начинаем услугу сразу
        if (canAcceptVisitor()) {
            System.out.println("\n=== Отладка startService ===");
            System.out.println("Посетитель: " + visitor.getName() + (visitor instanceof NPC ? " (NPC)" : " (Герой)"));
            System.out.println("Услуга: " + service.getName());
            System.out.println("Свободных мест: " + (maxVisitors - activeServices.size()));
            
            // Удаляем посетителя из очереди, если он там был
            boolean wasInQueue = serviceQueue.removeIf(req -> req.getVisitor().equals(visitor));
            if (wasInQueue) {
                System.out.println(visitor.getName() + " был удален из очереди");
            }
            
            activeServices.put(visitor, service);
            System.out.println(visitor.getName() + " начал использовать услугу " + service.getName());
            updateQueuePositions();
            // Показываем обновленный статус очереди
            showQueueStatus();
            System.out.println("=== Конец отладки startService ===\n");
            
            // Принудительно обновляем очередь NPC сразу после того, как герой занял место
            updateNPCVisits();
            ensureQueueNotEmpty();
        } else {
            System.out.println("\n=== Отладка startService (очередь) ===");
            System.out.println("Посетитель: " + visitor.getName() + (visitor instanceof NPC ? " (NPC)" : " (Герой)"));
            System.out.println("Услуга: " + service.getName());
            System.out.println("Свободных мест: " + (maxVisitors - activeServices.size()));
            System.out.println("Текущий размер очереди: " + serviceQueue.size());
            
            // Если нет свободных мест - добавляем в очередь
            // Удаляем старый запрос этого посетителя, если он был в очереди
            boolean wasInQueue = serviceQueue.removeIf(req -> req.getVisitor().equals(visitor));
            if (wasInQueue) {
                System.out.println(visitor.getName() + " был удален из очереди");
            }
            
            serviceQueue.offer(request);
            updateQueuePositions();
            System.out.println(visitor.getName() + " добавлен в очередь на услугу " + service.getName());
            
            // Если это герой, показываем специальное сообщение
            if (!(visitor instanceof NPC)) {
                System.out.println("\n=== " + visitor.getName() + " встал в очередь ===");
                System.out.println("Вы встали в очередь на услугу " + service.getName());
                System.out.println("Текущая позиция в очереди: " + request.getQueuePosition());
                
                // Показываем только NPC перед героем в очереди
                List<ServiceRequest> npcBeforeHero = serviceQueue.stream()
                    .filter(req -> req.getQueuePosition() < request.getQueuePosition() && req.getVisitor() instanceof NPC)
                    .collect(Collectors.toList());
                
                if (!npcBeforeHero.isEmpty()) {
                    System.out.println("Перед вами в очереди:");
                    for (ServiceRequest queuedRequest : npcBeforeHero) {
                        System.out.println("- " + queuedRequest.getVisitor().getName() + 
                                         " (" + queuedRequest.getService().getName() + ")");
                    }
                } else {
                    System.out.println("Перед вами нет посетителей в очереди.");
                }
                System.out.println("========================\n");
            } else {
                System.out.println(visitor.getName() + " встал в очередь на услугу " + service.getName() + 
                                 ". Позиция в очереди: " + request.getQueuePosition());
            }
            
            // Показываем обновленный статус очереди
            showQueueStatus();
            System.out.println("=== Конец отладки startService (очередь) ===\n");
        }
     
        return true;
    }
    
    private void updateQueuePositions() {
        int position = 1;
        for (ServiceRequest request : serviceQueue) {
            request.setQueuePosition(position++);
        }
    }
    
    public void processNextInQueue() {
        if (!serviceQueue.isEmpty() && canAcceptVisitor()) {
            ServiceRequest request = serviceQueue.poll();
            // Удаляем посетителя из очереди, если он там был
            serviceQueue.removeIf(req -> req.getVisitor().equals(request.getVisitor()));
            activeServices.put(request.getVisitor(), request.getService());
            System.out.println(request.getVisitor().getName() + " начал использовать услугу " + 
                             request.getService().getName());
            
            updateQueuePositions();
            
            // Гарантируем наличие NPC в очереди
            ensureQueueNotEmpty();
            
            // Показываем обновленный статус очереди
            showQueueStatus();
        }
    }
    
    public void updateServices() {
        long currentTime = GameTime.getGameTimeMs();
        
        // Обновляем активные услуги
        Iterator<Map.Entry<Character, Service>> it = activeServices.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Character, Service> entry = it.next();
            Service service = entry.getValue();
            Character visitor = entry.getKey();
            
            // Apply service effects when service is complete
            if (currentTime >= GameTime.gameMinutesToRealTime(service.getDurationMinutes())) {
                applyServiceEffects(visitor, service);
                it.remove();
                System.out.println(visitor.getName() + " завершил использование услуги " + service.getName());
                
                // Проверяем очередь после завершения услуги
                processNextInQueue();
                // Показываем обновленный статус очереди
                showQueueStatus();
            }
        }
        
        // Обновляем посещения NPC
        updateNPCVisits();
        
        // Показываем статус очереди после каждого обновления
        showQueueStatus();
    }
    
    private void showQueueStatus() {
        System.out.println("\n=== Текущая очередь в " + getName() + " ===");
        
        // Показываем активных посетителей
        System.out.println("Активные посетители (" + activeServices.size() + "/" + maxVisitors + "):");
        if (activeServices.isEmpty()) {
            System.out.println("- Нет активных посетителей");
        } else {
            for (Map.Entry<Character, Service> entry : activeServices.entrySet()) {
                System.out.println("- " + entry.getKey().getName() + 
                                 " (" + entry.getValue().getName() + ")");
            }
        }
        
        // Показываем очередь
        System.out.println("\nОжидающие в очереди:");
        List<ServiceRequest> npcInQueue = serviceQueue.stream()
            .filter(request -> request.getVisitor() instanceof NPC)
            .collect(Collectors.toList());
            
        if (npcInQueue.isEmpty()) {
            System.out.println("- Очередь пуста");
        } else {
            for (ServiceRequest request : npcInQueue) {
                System.out.println(request.getQueuePosition() + ". " + request.getVisitor().getName() + 
                                 " - " + request.getService().getName() + 
                                 " (ожидание: " + formatWaitingTime(request.getRequestTime()) + ")");
            }
        }
        System.out.println("========================\n");
    }
    
    private String formatWaitingTime(long requestTime) {
        long waitingTime = GameTime.getGameTimeMs() - requestTime;
        long minutes = waitingTime / (60 * 1000); // конвертируем миллисекунды в минуты
        
        if (minutes < 60) {
            return minutes + " мин.";
        } else {
            long hours = minutes / 60;
            minutes = minutes % 60;
            return hours + " ч. " + minutes + " мин.";
        }
    }
    
    protected void updateNPCVisits() {
        long currentTime = GameTime.getGameTimeMs();
        // Проверяем каждого регулярного посетителя
        for (NPC npc : regularVisitors) {
            // Если NPC не использует услугу и не в очереди
            if (!activeServices.containsKey(npc) && !isInQueue(npc)) {
                // Всегда пытаемся добавить NPC в очередь
                if (!availableServices.isEmpty()) {
                    int randomServiceIndex = new Random().nextInt(availableServices.size());
                    startService(npc, randomServiceIndex);
                }
            } else {
                if (activeServices.containsKey(npc)) {
                    System.out.println("NPC " + npc.getName() + " уже использует услугу, не добавляем в очередь");
                } else if (isInQueue(npc)) {
                    System.out.println("NPC " + npc.getName() + " уже в очереди, не добавляем повторно");
                }
            }
        }
        // Гарантируем наличие очереди из NPC
        ensureQueueNotEmpty();
    }
    
    private void ensureQueueNotEmpty() {
        // Если в очереди меньше 5 NPC, добавляем случайных NPC
        int npcInQueue = (int) serviceQueue.stream()
                .filter(request -> request.getVisitor() instanceof NPC)
                .count();
                
        System.out.println("\n=== Отладка очереди в " + getName() + " ===");
        System.out.println("Текущее количество NPC в очереди: " + npcInQueue);
        System.out.println("Всего регулярных посетителей: " + regularVisitors.size());
        System.out.println("Активных посетителей: " + activeServices.size());
                
        if (npcInQueue < 5) {
            System.out.println("Добавляем NPC в очередь...");
            // Добавляем NPC, пока не будет минимум 5 в очереди
            while (npcInQueue < 5 && !regularVisitors.isEmpty()) {
                // Выбираем случайного NPC, который не использует услугу и не в очереди
                List<NPC> availableNPCs = regularVisitors.stream()
                    .filter(npc -> !activeServices.containsKey(npc) && !isInQueue(npc))
                    .collect(Collectors.toList());
                    
                System.out.println("Доступных NPC для добавления в очередь: " + availableNPCs.size());
                    
                if (!availableNPCs.isEmpty()) {
                    NPC randomNPC = availableNPCs.get(new Random().nextInt(availableNPCs.size()));
                    int randomServiceIndex = new Random().nextInt(availableServices.size());
                    System.out.println("Пытаемся добавить " + randomNPC.getName() + " в очередь...");
                    if (startService(randomNPC, randomServiceIndex)) {
                        npcInQueue++;
                        System.out.println(randomNPC.getName() + " успешно добавлен в очередь");
                    } else {
                        System.out.println("Не удалось добавить " + randomNPC.getName() + " в очередь");
                    }
                } else {
                    System.out.println("Нет доступных NPC, выбираем любого NPC...");
                    // Если нет доступных NPC, выбираем любого NPC и добавляем его в очередь
                    NPC randomNPC = regularVisitors.get(new Random().nextInt(regularVisitors.size()));
                    // Удаляем его из активных услуг, если он там есть
                    activeServices.remove(randomNPC);
                    // Удаляем его из очереди, если он там есть
                    serviceQueue.removeIf(req -> req.getVisitor().equals(randomNPC));
                    // Добавляем в очередь
                    int randomServiceIndex = new Random().nextInt(availableServices.size());
                    System.out.println("Пытаемся добавить " + randomNPC.getName() + " в очередь (принудительно)...");
                    if (startService(randomNPC, randomServiceIndex)) {
                        npcInQueue++;
                        System.out.println(randomNPC.getName() + " успешно добавлен в очередь");
                    } else {
                        System.out.println("Не удалось добавить " + randomNPC.getName() + " в очередь");
                    }
                }
            }
        }
        System.out.println("=== Конец отладки очереди ===\n");
    }
    
    private boolean isInQueue(Character visitor) {
        return serviceQueue.stream()
                .anyMatch(request -> request.getVisitor().equals(visitor));
    }
    
    public void addRegularVisitor(NPC npc) {
        if (!regularVisitors.contains(npc)) {
            regularVisitors.add(npc);
            System.out.println(npc.getName() + " стал регулярным посетителем " + getName());
        }
    }
    
    public void removeRegularVisitor(NPC npc) {
        if (regularVisitors.remove(npc)) {
            System.out.println(npc.getName() + " больше не является регулярным посетителем " + getName());
        }
    }
    
    protected abstract void applyServiceEffects(Character visitor, Service service);
    
    public String getStatus() {
        StringBuilder status = new StringBuilder();
        status.append(getName()).append(" - Занятость: ").append(activeServices.size())
              .append("/").append(maxVisitors).append("\n");
        
        if (!activeServices.isEmpty()) {
            status.append("Текущие посетители:\n");
            for (Map.Entry<Character, Service> entry : activeServices.entrySet()) {
                Character visitor = entry.getKey();
                Service service = entry.getValue();
                status.append("- ").append(visitor.getName())
                      .append(" (").append(service.getName()).append(")\n");
            }
        }
        
        if (!serviceQueue.isEmpty()) {
            status.append("\nОчередь:\n");
            for (ServiceRequest request : serviceQueue) {
                status.append(request.getQueuePosition()).append(". ")
                      .append(request.getVisitor().getName())
                      .append(" - ").append(request.getService().getName()).append("\n");
            }
        }
        
        if (!regularVisitors.isEmpty()) {
            status.append("\nРегулярные посетители:\n");
            for (NPC npc : regularVisitors) {
                status.append("- ").append(npc.getName())
                      .append(" (").append(npc.getOccupation()).append(")\n");
            }
        }
        
        return status.toString();
    }

    public void provideService(Character hero, Service service) {
        if (!canAcceptVisitor()) {
            System.out.println("Извините, " + getName() + " сейчас заполнен.");
            return;
        }

        currentVisitors++;
        try {
            applyServiceEffects(hero, service);
            System.out.println(hero.getName() + " использует услугу " + service.getName() + " в " + getName());
        } finally {
            currentVisitors--;
        }
    }
} 