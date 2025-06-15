package org.example.game.build;

import org.example.game.person.NPC;
import java.io.Serializable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Random;
import java.util.List;

public class ServiceQueue implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Random random = new Random();

    private final int queueId;
    private final String buildingName;
    private final ConcurrentLinkedQueue<NPC> queue;
    private final int maxQueueSize;
    private NPC currentCustomer;
    private long lastNpcSpawnTime; // Время последнего появления NPC в игровых минутах
    private static final long MIN_NPC_SPAWN_INTERVAL = 5; // Минимальный интервал между появлениями NPC (в игровых минутах)
    private static final long MAX_NPC_SPAWN_INTERVAL = 15; // Максимальный интервал между появлениями NPC (в игровых минутах)
    private static final double NPC_SPAWN_CHANCE = 0.3; // Шанс появления NPC при проверке

    public ServiceQueue(String buildingName, int maxQueueSize) {
        this.queueId = random.nextInt(1000);
        this.buildingName = buildingName;
        this.queue = new ConcurrentLinkedQueue<>();
        this.maxQueueSize = maxQueueSize;
        this.lastNpcSpawnTime = 0;
    }

    public void updateQueue(long currentGameTime) {
        // Проверяем, не пора ли добавить нового NPC
        if (shouldSpawnNPC(currentGameTime)) {
            tryAddRandomNPC(currentGameTime);
        }

        // Проверяем завершение услуги для текущего клиента
        if (currentCustomer != null && currentCustomer.isServiceCompleted(currentGameTime)) {
            System.out.println(currentCustomer.getName() + " покинул " + buildingName);
            currentCustomer = null;
            processNextCustomer(currentGameTime);
        }
    }

    private boolean shouldSpawnNPC(long currentGameTime) {
        if (currentGameTime - lastNpcSpawnTime < MIN_NPC_SPAWN_INTERVAL) {
            return false;
        }
        if (currentGameTime - lastNpcSpawnTime > MAX_NPC_SPAWN_INTERVAL) {
            return true;
        }
        return random.nextDouble() < NPC_SPAWN_CHANCE;
    }

    private void tryAddRandomNPC(long currentGameTime) {
        NPC npc = NPC.generateRandomNPC();
        
        // Получаем список доступных услуг из здания
        Building building = getBuilding();
        if (building != null) {
            List<Service> availableServices = building.getAvailableServices();
            if (!availableServices.isEmpty()) {
                // Выбираем случайную услугу
                Service randomService = availableServices.get(random.nextInt(availableServices.size()));
                npc.setSelectedService(randomService);
            }
        }
        
        if (addToQueue(npc, currentGameTime)) {
            lastNpcSpawnTime = currentGameTime;
            System.out.println(npc.getName() + " встал в очередь в " + buildingName);
        }
    }

    private Building getBuilding() {
        // Находим здание по имени в списке всех зданий
        return Building.getAllBuildings().stream()
            .filter(b -> b.getName().equals(buildingName))
            .findFirst()
            .orElse(null);
    }

    public boolean addToQueue(NPC npc, long currentGameTime) {
        if (npc == null) {
            return false;
        }
        
        // Всегда добавляем в очередь, даже если она полная
        queue.offer(npc);
        System.out.println(npc.getName() + " встал в очередь на услугу: " + 
            (npc.getSelectedService() != null ? npc.getSelectedService().getName() : "не выбрана"));
        
        // Если это первый в очереди, сразу начинаем обслуживание
        if (queue.size() == 1) {
            processNextCustomer(currentGameTime);
        }
        
        return true;
    }

    public void processNextCustomer(long currentGameTime) {
        if (currentCustomer == null && !queue.isEmpty()) {
            currentCustomer = queue.poll();
            if (currentCustomer != null) {
                System.out.println(currentCustomer.getName() + " подошел к обслуживанию в " + buildingName);
            }
        }
    }

    public void startServiceForCurrentCustomer(Service service, long currentGameTime) {
        if (currentCustomer != null) {
            currentCustomer.startService(service, currentGameTime);
            System.out.println(currentCustomer.getName() + " начал получать услугу: " + service.getName());
        }
    }

    public String getQueueStatus(long currentGameTime) {
        StringBuilder status = new StringBuilder();
        status.append("\n=== Очередь в ").append(buildingName).append(" ===\n");
        
        if (queue.isEmpty() && currentCustomer == null) {
            status.append("Очередь пуста\n");
            return status.toString();
        }

        // Показываем текущего клиента
        if (currentCustomer != null) {
            status.append("Сейчас обслуживается: ").append(currentCustomer.getName())
                  .append(" (Услуга: ").append(currentCustomer.getSelectedService().getName()).append(")\n");
        }

        // Показываем очередь
        if (!queue.isEmpty()) {
            status.append("\nОжидают в очереди:\n");
            int position = 1;
            for (NPC npc : queue) {
                status.append(position++).append(". ").append(npc.getName())
                      .append(" (Услуга: ").append(npc.getSelectedService().getName()).append(")\n");
            }
        }

        return status.toString();
    }

    public boolean isQueueFull() {
        // Убираем ограничение на размер очереди
        return false;
    }

    public int getQueueSize() {
        return queue.size();
    }

    public NPC getCurrentCustomer() {
        return currentCustomer;
    }

    public void clearQueue() {
        queue.clear();
        if (currentCustomer != null) {
            currentCustomer.cancelService();
            currentCustomer = null;
        }
    }
} 