package org.example.game.person;

import org.example.game.build.Service;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Random;

public class NPC implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final AtomicInteger idCounter = new AtomicInteger(0);
    private static final Random random = new Random();

    private final int id;
    private final String name;
    private Service selectedService;
    private volatile boolean serviceCompleted;
    private Thread serviceThread;
    private long serviceStartTime; // Время начала услуги в игровых минутах
    private long serviceEndTime;   // Время окончания услуги в игровых минутах

    public NPC(String name) {
        this.id = idCounter.incrementAndGet();
        this.name = name;
        this.serviceCompleted = false;
    }

    public void startService(Service service, long currentGameTime) {
        if (serviceThread != null && serviceThread.isAlive()) {
            return;
        }

        this.selectedService = service;
        this.serviceCompleted = false;
        this.serviceStartTime = currentGameTime;
        this.serviceEndTime = currentGameTime + service.getDurationMinutes();

        serviceThread = new Thread(() -> {
            try {
                // Ждем, пока не наступит время окончания услуги
                while (currentGameTime < serviceEndTime) {
                    Thread.sleep(100); // Проверяем каждые 100мс
                }
                serviceCompleted = true;
                System.out.println(name + " завершил услугу: " + service.getName());
            } catch (InterruptedException e) {
                System.out.println(name + " прервал услугу: " + service.getName());
                Thread.currentThread().interrupt();
            }
        });
        serviceThread.start();
    }

    public boolean isServiceCompleted(long currentGameTime) {
        return currentGameTime >= serviceEndTime;
    }

    public void cancelService() {
        if (serviceThread != null && serviceThread.isAlive()) {
            serviceThread.interrupt();
            serviceCompleted = false;
            selectedService = null;
        }
    }

    public Service getSelectedService() {
        return selectedService;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public long getServiceStartTime() {
        return serviceStartTime;
    }

    public long getServiceEndTime() {
        return serviceEndTime;
    }

    public long getRemainingTime(long currentGameTime) {
        return Math.max(0, serviceEndTime - currentGameTime);
    }

    public void setSelectedService(Service service) {
        this.selectedService = service;
        this.serviceCompleted = false;
        this.serviceStartTime = 0;
        this.serviceEndTime = 0;
    }

    // Метод для генерации случайного NPC
    public static NPC generateRandomNPC() {
        String[] firstNames = {"Иван", "Петр", "Мария", "Анна", "Алексей", "Елена", "Дмитрий", "Ольга"};
        String[] lastNames = {"Иванов", "Петров", "Сидоров", "Козлова", "Смирнов", "Кузнецова", "Попов", "Васильева"};
        
        String name = firstNames[random.nextInt(firstNames.length)] + " " + 
                     lastNames[random.nextInt(lastNames.length)];
        return new NPC(name);
    }

    @Override
    public String toString() {
        return name + (selectedService != null ? " - " + selectedService.getName() : "");
    }
} 