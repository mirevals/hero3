package org.example.entities;

public class Player {
    private final Hero hero;
    private int gold;

    public Player(Hero hero) {
        this.hero = hero;
        this.gold = 0;
    }

    public int getGold() {
        return gold;
    }

    public void addGold(int amount) {
        gold += amount;
        System.out.println("Вы получили " + amount + " золота! Текущее количество золота: " + gold);
    }

    public Hero getHero() {
        return hero;
    }
}