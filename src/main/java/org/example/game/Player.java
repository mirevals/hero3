package org.example.game;

import java.io.Serializable;

public class Player implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private final String name;
    private int gold;  // Количество золота у игрока

    public Player(String name, int initialGold) {
        this.name = name;
        this.gold = initialGold;  // Инициализация с начальным количеством золота
    }

    // Метод для получения имени игрока
    public String getName() {
        return name;
    }

    // Метод для получения текущего количества золота
    public int getGold() {
        return gold;
    }

    // Метод для добавления золота
    public void addGold(int amount) {
        if (amount > 0) {
            gold += amount;
            System.out.println(amount + " золота добавлено.");
        } else {
            System.out.println("Невозможно добавить отрицательное количество золота.");
        }
    }

    // Метод для уменьшения золота
    public boolean spendGold(int amount) {
        if (amount > 0 && gold >= amount) {
            gold -= amount;
            System.out.println(amount + " золота потрачено.");
            return true;
        } else if (amount <= 0) {
            System.out.println("Невозможно потратить отрицательное количество золота.");
        } else {
            System.out.println("Недостаточно золота.");
        }
        return false;
    }

    // Метод для проверки, хватает ли золота на покупку
    public boolean hasEnoughGold(int amount) {
        return gold >= amount;
    }
}