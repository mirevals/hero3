package org.example.game;

// Абстрактный класс для всех строений
public abstract class Building {

    private final String name;  // Имя строения
    private final int cost;  // Стоимость строительства
    private boolean isConstructed;  // Строение построено или нет

    // Конструктор
    public Building(String name, int cost) {
        this.name = name;
        this.cost = cost;
        this.isConstructed = false;  // Строение ещё не построено
    }

    // Метод для постройки строения
    public void construct() {
        if (!isConstructed) {
            isConstructed = true;
            System.out.println(name + " построено.");
        } else {
            System.out.println(name + " уже построено.");
        }
    }

    // Метод для получения имени строения
    public String getName() {
        return name;
    }

    // Метод для получения стоимости строения
    public int getCost() {
        return cost;
    }

    // Метод для проверки, построено ли строение
    public boolean isConstructed() {
        return isConstructed;
    }

    // Абстрактный метод для выполнения действия, связанного со строением
    public abstract void executeAction();
}