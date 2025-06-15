package org.example.game.build;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import org.example.game.person.NPC;

public class Building implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final List<Building> allBuildings = new ArrayList<>();

    private String name;
    private boolean canRecruitUnits;
    protected char symbol;
    protected int x;
    protected int y;
    protected int maxVisitors;
    protected ConcurrentHashMap<String, Service> services;
    protected ServiceQueue serviceQueue;

    public Building(String name, boolean canRecruitUnits) {
        this.name = name;
        this.canRecruitUnits = canRecruitUnits;
        this.services = new ConcurrentHashMap<>();
        this.serviceQueue = new ServiceQueue(name, maxVisitors);
        allBuildings.add(this);
    }

    public static List<Building> getAllBuildings() {
        return new ArrayList<>(allBuildings);
    }

    public static void clearAllBuildings() {
        allBuildings.clear();
    }

    public String getName() {
        return name;
    }

    public boolean canRecruitUnits() {
        return canRecruitUnits;
    }

    public char getSymbol() {
        return symbol;
    }

    public void placeAt(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getMaxVisitors() {
        return maxVisitors;
    }

    public boolean canPlaceAt(int x, int y, char[][] map) {
        // Проверяем только, что клетка находится в пределах карты и пуста
        return x >= 0 && y >= 0 && x < map.length && y < map[0].length && map[y][x] == ' ';
    }

    public List<Service> getAvailableServices() {
        return new ArrayList<>(services.values());
    }

    public void updateQueue(long currentGameTime) {
        if (serviceQueue != null) {
            serviceQueue.updateQueue(currentGameTime);
        }
    }

    public boolean addToQueue(NPC npc, long currentGameTime) {
        return serviceQueue.addToQueue(npc, currentGameTime);
    }

    public void startServiceForCurrentCustomer(Service service, long currentGameTime) {
        if (serviceQueue != null) {
            serviceQueue.startServiceForCurrentCustomer(service, currentGameTime);
        }
    }

    public String getQueueStatus(long currentGameTime) {
        return serviceQueue != null ? serviceQueue.getQueueStatus(currentGameTime) : "Очередь недоступна";
    }

    public boolean isQueueFull() {
        return serviceQueue != null && serviceQueue.isQueueFull();
    }

    public int getQueueSize() {
        return serviceQueue != null ? serviceQueue.getQueueSize() : 0;
    }

    public NPC getCurrentCustomer() {
        return serviceQueue != null ? serviceQueue.getCurrentCustomer() : null;
    }

    public void clearQueue() {
        if (serviceQueue != null) {
            serviceQueue.clearQueue();
        }
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        // Инициализируем очередь после десериализации
        this.serviceQueue = new ServiceQueue(name, maxVisitors);
    }
}