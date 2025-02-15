package org.example.entities;

import org.example.entities.units.Unit;
import org.example.game.GameMap;

import java.util.ArrayList;
import java.util.List;

public class Hero {
    private String name;
    private int health;
    private int attack;
    private Position position;
    private List<Unit> army; // Список юнитов в армии героя
    private int movementRange; // Добавляем поле для дальности перемещения
    private int damage;

    public Hero(String name, int health, int attack, Position position) {
        this.name = name;
        this.health = health;
        this.damage = damage;  // Use damage here
        this.army = new ArrayList<>();
        this.position = position;
        this.movementRange = 5;
    }

    // Метод для добавления юнита в армию героя
    public void addUnit(Unit unit) {
        army.add(unit);
    }

    // Метод для удаления юнита из армии героя
    public void removeUnit(Unit unit) {
        army.remove(unit);
    }

    // Метод для проверки, есть ли юниты в армии
    public boolean hasArmy() {
        return !army.isEmpty();
    }

    // Геттеры и сеттеры
    public String getName() {
        return name;
    }

    public int getHealth() {
        return health;
    }

    public int getAttack() {
        return attack;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public List<Unit> getArmy() {
        return army;
    }

    // Метод для проверки жив ли герой
    public boolean isAlive() {
        return health > 0;
    }

    // Метод для уменьшения здоровья героя при атаке
    public void takeDamage(int amount) {
        health -= amount;
        if (health < 0) {
            health = 0;
        }
        System.out.println(name + " получил " + amount + " урона. Оставшееся ХП: " + health);
    }

    // Метод для перемещения героя (если это необходимо)
    public void move(int x, int y, GameMap gameMap) {
        Position newPosition = new Position(x, y);
        if (gameMap.isWalkable(newPosition)) {
            this.position = newPosition;
        }
    }

    // Метод для атаки (например, атакуя другого героя или юнита)
    public void attack(Hero enemyHero) {
        if (enemyHero.hasArmy()) {
            // Логика атаки на армию противника
            System.out.println(this.name + " атакует армию противника!");
        } else {
            // Атака на самого героя
            enemyHero.takeDamage(this.attack);
            System.out.println(this.name + " атакует героя " + enemyHero.getName());
        }
    }

    public int getMovementRange() {
        return movementRange;
    }

    public void setMovementRange(int movementRange) {
        this.movementRange = movementRange;
    }

    // Метод для увеличения дальности перемещения
    public void increaseMovementRange(int increment) {
        this.movementRange += increment;
    }
    // Getter for damage
    public int getDamage() {
        return damage;
    }
}