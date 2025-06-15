package org.example.game.build;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.ArrayList;

public class Building implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private boolean canRecruitUnits;
    protected char symbol;
    protected int x;
    protected int y;
    protected int maxVisitors;
    protected ConcurrentHashMap<String, Service> services;

    public Building(String name, boolean canRecruitUnits) {
        this.name = name;
        this.canRecruitUnits = canRecruitUnits;
        this.services = new ConcurrentHashMap<>();
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
}