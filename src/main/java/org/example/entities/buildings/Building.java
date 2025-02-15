package org.example.entities.buildings;

public abstract class Building {
    private String name;
    private int cost;

    public Building(String name, int cost) {
        this.name = name;
        this.cost = cost;
    }

    public String getName() {
        return name;
    }

    public int getCost() {
        return cost;
    }

    // Абстрактный метод для строительства
    public abstract void build();

    // Абстрактный метод для применения эффекта (если это нужно)
    public void applyEffect() {
        // Реализация по умолчанию (если нужна)
        System.out.println("Эффект от здания применяется.");
    }
}