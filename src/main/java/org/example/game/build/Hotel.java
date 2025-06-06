package org.example.game.build;

import org.example.game.person.Character;
import org.example.game.person.Hero;
import org.example.game.person.NPC;
import org.example.game.person.Team;
import org.example.game.person.Unit;
import org.example.game.map.Position;
import org.example.game.time.GameTime;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

public class Hotel extends ServiceBuilding {
    private static final String HOTEL_NAME = "Отель «У погибшего альпиниста»";
    private static final int ROOMS = 5;
    private static final int MAX_QUEUE_SIZE = 10;
    private static final long NPC_UPDATE_INTERVAL = 5000; // 5 секунд реального времени
    private long lastNPCUpdateTime;
    private final Random random = new Random();
    
    public Hotel() {
        super(HOTEL_NAME, ROOMS);
        initializeServices();
        initializeRegularVisitors();
        this.lastNPCUpdateTime = GameTime.getGameTimeMs();
    }
    
    private void initializeServices() {
        // Короткий отдых: +2 здоровья за 1 день
        Map<String, Integer> shortRestBonuses = new HashMap<>();
        shortRestBonuses.put("health", 2);
        addService(new Service("Короткий отдых", 48 * 60, 50, shortRestBonuses)); // 48 часов = 2 дня
        
        // Длинный отдых: +3 здоровья за 3 дня
        Map<String, Integer> longRestBonuses = new HashMap<>();
        longRestBonuses.put("health", 3);
        addService(new Service("Длинный отдых", 144 * 60, 120, longRestBonuses)); // 144 часа = 6 дней
    }
    
    private void initializeRegularVisitors() {
        // Создаем группы NPC с разной частотой посещений
        // Группа 1: Очень частые посетители (каждые 5 секунд)
        addRegularVisitor(new NPC("Местный житель", "Охотник", 1, Team.NEUTRAL, 70, 15, 4, 4, 1, new Position(0, 0), 5));
        addRegularVisitor(new NPC("Лесник", "Смотритель", 2, Team.NEUTRAL, 100, 20, 6, 6, 2, new Position(0, 0), 5));
        addRegularVisitor(new NPC("Дровосек", "Работник", 1, Team.NEUTRAL, 80, 15, 5, 5, 1, new Position(0, 0), 5));
        addRegularVisitor(new NPC("Рыбак", "Местный", 1, Team.NEUTRAL, 75, 15, 4, 4, 1, new Position(0, 0), 5));
        addRegularVisitor(new NPC("Пастух", "Фермер", 1, Team.NEUTRAL, 85, 15, 5, 5, 1, new Position(0, 0), 5));
        
        // Группа 2: Частые посетители (каждые 10 секунд)
        addRegularVisitor(new NPC("Старый альпинист", "Пенсионер", 1, Team.NEUTRAL, 100, 30, 5, 5, 2, new Position(0, 0), 10));
        addRegularVisitor(new NPC("Горный проводник", "Гид", 2, Team.NEUTRAL, 150, 40, 8, 8, 2, new Position(0, 0), 10));
        addRegularVisitor(new NPC("Турист", "Путешественник", 1, Team.NEUTRAL, 80, 20, 4, 4, 1, new Position(0, 0), 10));
        addRegularVisitor(new NPC("Краевед", "Историк", 2, Team.NEUTRAL, 110, 25, 6, 6, 1, new Position(0, 0), 10));
        addRegularVisitor(new NPC("Фотограф", "Художник", 1, Team.NEUTRAL, 90, 25, 5, 5, 1, new Position(0, 0), 10));
        
        // Группа 3: Средняя частота (каждые 15 секунд)
        addRegularVisitor(new NPC("Горный спасатель", "Спасатель", 3, Team.NEUTRAL, 200, 50, 10, 10, 2, new Position(0, 0), 15));
        addRegularVisitor(new NPC("Метеоролог", "Ученый", 2, Team.NEUTRAL, 120, 35, 6, 6, 1, new Position(0, 0), 15));
        addRegularVisitor(new NPC("Геолог", "Исследователь", 3, Team.NEUTRAL, 180, 45, 9, 9, 2, new Position(0, 0), 15));
        addRegularVisitor(new NPC("Биолог", "Ученый", 2, Team.NEUTRAL, 130, 30, 7, 7, 1, new Position(0, 0), 15));
        addRegularVisitor(new NPC("Археолог", "Исследователь", 2, Team.NEUTRAL, 140, 35, 7, 7, 1, new Position(0, 0), 15));
        
        // Группа 4: Редкие посетители (каждые 20 секунд)
        addRegularVisitor(new NPC("Торговец", "Купец", 2, Team.NEUTRAL, 160, 40, 8, 8, 1, new Position(0, 0), 20));
        addRegularVisitor(new NPC("Картограф", "Исследователь", 2, Team.NEUTRAL, 150, 35, 7, 7, 1, new Position(0, 0), 20));
        addRegularVisitor(new NPC("Альпинист", "Спортсмен", 3, Team.NEUTRAL, 190, 45, 9, 9, 2, new Position(0, 0), 20));
        addRegularVisitor(new NPC("Экскурсовод", "Гид", 2, Team.NEUTRAL, 130, 30, 6, 6, 1, new Position(0, 0), 20));
        addRegularVisitor(new NPC("Охотник", "Промысловик", 2, Team.NEUTRAL, 170, 40, 8, 8, 2, new Position(0, 0), 20));
    }
    
    @Override
    public void provideService(Character visitor, Service service) {
        // Обновляем очередь NPC перед оказанием услуги
        updateNPCQueue();
        
        // Если это герой
        if (visitor instanceof Hero) {
            // Сначала показываем текущую очередь NPC
            if (!serviceQueue.isEmpty()) {
                System.out.println("\nПеред вами в очереди:");
                int position = 1;
                for (ServiceRequest request : serviceQueue) {
                    if (request.getRequester() instanceof NPC) {
                        NPC npc = (NPC) request.getRequester();
                        System.out.println(position + ". " + npc.getName() + " (" + npc.getOccupation() + ") - " + 
                                         request.getService().getName());
                        position++;
                    }
                }
                System.out.println("\nВы должны подождать, пока эти посетители получат свои услуги.");
            }

            // Создаем запрос на услугу для героя
            ServiceRequest heroRequest = new ServiceRequest(visitor, service);
            
            // Если есть NPC в очереди, добавляем героя в конец
            if (!serviceQueue.isEmpty()) {
                serviceQueue.add(heroRequest);
                serviceRequestCounts.put(service, serviceRequestCounts.get(service) + 1);
                
                // Обрабатываем очередь до тех пор, пока не дойдет очередь до героя
                while (!serviceQueue.isEmpty() && serviceQueue.peek() != heroRequest) {
                    processNextInQueue();
                }
            } else {
                // Если очередь пуста, сразу обрабатываем запрос героя
                serviceQueue.add(heroRequest);
                serviceRequestCounts.put(service, serviceRequestCounts.get(service) + 1);
            }
            
            // Когда подошла очередь героя, оказываем ему услугу
            if (!serviceQueue.isEmpty() && serviceQueue.peek() == heroRequest) {
                serviceQueue.poll(); // Удаляем запрос героя из очереди
                currentVisitors++;
                heroRequest.startService();
                try {
                    applyServiceEffects(visitor, service);
                    System.out.println(visitor.getName() + " использует услугу " + service.getName() + " в " + getName());
                } finally {
                    heroRequest.endService();
                    currentVisitors--;
                    updateServiceStatistics(heroRequest);
                }
            }
        } else {
            // Для NPC используем стандартное поведение
            super.provideService(visitor, service);
        }
    }
    
    private void updateNPCQueue() {
        // Проверяем, нужно ли добавить новых NPC в очередь
        for (NPC npc : regularVisitors) {
            if (npc.getLastVisitTime() == 0 || 
                GameTime.getGameTimeMs() - npc.getLastVisitTime() >= npc.getVisitFrequency()) {
                
                // Выбираем случайную услугу для NPC
                Service service = getRandomService();
                
                // Создаем запрос на услугу
                ServiceRequest request = new ServiceRequest(npc, service);
                
                // Добавляем в очередь только если есть место
                if (serviceQueue.size() < MAX_QUEUE_SIZE) {
                    serviceQueue.add(request);
                    serviceRequestCounts.put(service, serviceRequestCounts.get(service) + 1);
                    System.out.println(npc.getName() + " (" + npc.getOccupation() + 
                                     ") присоединился к очереди за услугой " + service.getName());
                }
            }
        }
    }
    
    private Service getRandomService() {
        if (availableServices.isEmpty()) {
            return null;
        }
        return availableServices.get(random.nextInt(availableServices.size()));
    }
    
    @Override
    public void processNextInQueue() {
        if (serviceQueue.isEmpty() || !canAcceptVisitor()) {
            return;
        }

        ServiceRequest request = serviceQueue.poll();
        if (request != null) {
            Character visitor = request.getRequester();
            Service service = request.getService();
            
            // Обновляем время последнего посещения для NPC
            if (visitor instanceof NPC) {
                ((NPC) visitor).setLastVisitTime(GameTime.getGameTimeMs());
            }
            
            // Начинаем оказание услуги
            currentVisitors++;
            request.startService();
            try {
                // Применяем эффекты услуги только один раз
                applyServiceEffects(visitor, service);
                System.out.println(visitor.getName() + " (" + 
                    (visitor instanceof NPC ? ((NPC) visitor).getOccupation() : "Герой") + 
                    ") использует услугу " + service.getName());
            } finally {
                request.endService();
                currentVisitors--;
                updateServiceStatistics(request);
            }
        }
    }
    
    private void showCurrentQueueStatus() {
        if (!serviceQueue.isEmpty()) {
            System.out.println("\nТекущая очередь в отеле:");
            int position = 1;
            boolean hasNPCs = false;
            
            // Сначала показываем NPC
            for (ServiceRequest request : serviceQueue) {
                if (request.getRequester() instanceof NPC) {
                    hasNPCs = true;
                    NPC npc = (NPC) request.getRequester();
                    System.out.println(position + ". " + npc.getName() + " (" + npc.getOccupation() + ") - " + 
                                     request.getService().getName());
                    position++;
                }
            }
            
            // Затем показываем героя, если он есть
            for (ServiceRequest request : serviceQueue) {
                if (request.getRequester() instanceof Hero) {
                    System.out.println(position + ". " + request.getRequester().getName() + " (Герой) - " + 
                                     request.getService().getName());
                    position++;
                }
            }
            
            if (!hasNPCs) {
                System.out.println("В очереди только герой.");
            }
            System.out.println(); // Добавляем пустую строку для лучшей читаемости
        } else {
            System.out.println("\nВ данный момент очередь пуста.");
        }
    }
    
    @Override
    protected void applyServiceEffects(Character visitor, Service service) {
        // Применяем эффекты услуги только один раз
        if (service.getName().equals("Длинный отдых")) {
            visitor.takeDamage(-3); // Восстановление здоровья
            System.out.println(visitor.getName() + " получил бонусы от услуги " + service.getName() + ":");
            System.out.println("- health: +3");
        } else if (service.getName().equals("Короткий отдых")) {
            visitor.takeDamage(-1);
            System.out.println(visitor.getName() + " получил бонусы от услуги " + service.getName() + ":");
            System.out.println("- health: +1");
        }
    }
    
    public String getDescription() {
        return "Отель расположен в живописном месте у подножия гор. " +
               "Предлагает уютные номера и различные услуги для отдыха. " +
               "Максимальное количество гостей: " + maxVisitors + ". " +
               "Регулярно посещается местными жителями и путешественниками.";
    }

    // Обновляем позиции NPC при изменении позиции отеля
    public void updateNPCPositions(Position newPosition) {
        for (NPC npc : regularVisitors) {
            npc.setPosition(newPosition.getX(), newPosition.getY());
        }
    }
} 