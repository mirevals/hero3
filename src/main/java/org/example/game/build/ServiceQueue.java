package org.example.game.build;

import org.example.game.person.NPC;
import java.io.Serializable;
import java.io.IOException;
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
    private transient Building building; // Добавляем ссылку на здание

    public ServiceQueue(Building building, int maxQueueSize) {
        this.queueId = random.nextInt(1000);
        this.building = building;
        this.buildingName = building.getName();
        this.queue = new ConcurrentLinkedQueue<>();
        this.maxQueueSize = maxQueueSize;
        this.lastNpcSpawnTime = 0;
    }

    public void updateQueue(long currentGameTime) {
        // Проверяем завершение услуги для текущего клиента
        if (currentCustomer != null) {
            if (currentCustomer.isServiceCompleted(currentGameTime)) {
                System.out.println(currentCustomer.getName() + " покинул " + buildingName + " (услуга завершена)");
                currentCustomer = null;
                processNextCustomer(currentGameTime);
            }
        } else {
            // Если нет текущего клиента, обрабатываем следующего
            processNextCustomer(currentGameTime);
        }

        // Добавляем нового NPC только если очередь пуста или почти пуста
        if (queue.size() < 2) {
            tryAddRandomNPC(currentGameTime);
        }
    }

    private boolean shouldSpawnNPC(long currentGameTime) {
        // Этот метод больше не используется, так как NPC добавляются при каждом обновлении
        return false;
    }

    private void tryAddRandomNPC(long currentGameTime) {
        if (building == null) {
            System.out.println("Ошибка: здание не инициализировано");
            return;
        }

        List<Service> availableServices = building.getAvailableServices();
        if (availableServices.isEmpty()) {
            System.out.println("Нет доступных услуг в " + buildingName);
            return;
        }

        // Создаем NPC и выбираем случайную услугу
        NPC npc = NPC.generateRandomNPC();
        Service randomService = availableServices.get(random.nextInt(availableServices.size()));
        npc.setSelectedService(randomService);

        // Добавляем в очередь
        if (addToQueue(npc, currentGameTime)) {
            lastNpcSpawnTime = currentGameTime;
            System.out.println(npc.getName() + " встал в очередь на услугу: " + randomService.getName());
        }
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        // Восстанавливаем ссылку на здание после десериализации
        this.building = Building.getAllBuildings().stream()
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
            if (currentCustomer != null && currentCustomer.getSelectedService() != null) {
                System.out.println(currentCustomer.getName() + " подошел к обслуживанию в " + buildingName);
                startServiceForCurrentCustomer(currentCustomer.getSelectedService(), currentGameTime);
            } else {
                // Если у клиента нет выбранной услуги, пропускаем его
                System.out.println(currentCustomer.getName() + " покинул " + buildingName + " (не выбрана услуга)");
                currentCustomer = null;
                processNextCustomer(currentGameTime); // Обрабатываем следующего клиента
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
            long remainingTime = currentCustomer.getRemainingTime(currentGameTime);
            status.append("Сейчас обслуживается: ").append(currentCustomer.getName())
                  .append(" (Услуга: ").append(currentCustomer.getSelectedService().getName()).append(")\n")
                  .append("Осталось времени: ").append(formatTime(remainingTime)).append("\n");
        }

        // Показываем очередь
        if (!queue.isEmpty()) {
            status.append("\nОжидают в очереди:\n");
            int position = 1;
            long totalWaitingTime = 0;
            
            // Сначала считаем общее время ожидания
            for (NPC npc : queue) {
                if (npc.getSelectedService() != null) {
                    totalWaitingTime += npc.getSelectedService().getDurationMinutes(); // durationMinutes теперь в секундах
                }
            }
            
            // Если есть текущий клиент, добавляем его время
            if (currentCustomer != null && currentCustomer.getSelectedService() != null) {
                totalWaitingTime += currentCustomer.getRemainingTime(currentGameTime);
            }
            
            // Показываем каждого в очереди
            for (NPC npc : queue) {
                if (npc.getSelectedService() != null) {
                    status.append(position++).append(". ").append(npc.getName())
                          .append(" (Услуга: ").append(npc.getSelectedService().getName()).append(")\n");
                }
            }
            
            // Показываем общее время ожидания
            if (totalWaitingTime > 0) {
                status.append("\nПримерное время ожидания: ").append(formatTime(totalWaitingTime)).append("\n");
            }
        }

        return status.toString();
    }

    private String formatTime(long seconds) {
        if (seconds < 60) {
            return seconds + " сек";
        } else {
            int minutes = (int) (seconds / 60);
            int secs = (int) (seconds % 60);
            if (secs == 0) {
                return minutes + " мин";
            } else {
                return minutes + " мин " + secs + " сек";
            }
        }
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