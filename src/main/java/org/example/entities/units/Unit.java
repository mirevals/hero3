package org.example.entities.units;

import org.example.entities.Position;
import org.example.game.GameMap;

public class Unit {
    private final String name;
    private int health;
    private final int maxHealth;
    private final int damage;
    private final int movementRange;
    private final int attackRange;
    private final Team team;  // Поле для команды
    private Position position; // Позиция юнита на карте

    public Unit(String name, int health, int damage, int movementRange, int attackRange, Team team) {
        this.name = name;
        this.health = health;
        this.maxHealth = health;
        this.damage = damage;
        this.movementRange = movementRange;
        this.attackRange = attackRange;
        this.team = team;
    }

    public String getName() {
        return name;
    }

    public int getHealth() {
        return health;
    }

    public int getDamage() {
        return damage;
    }

    public int getMovementRange() {
        return movementRange;
    }

    public int getAttackRange() {
        return attackRange;
    }

    public Team getTeam() {
        return team;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public void takeDamage(int amount) {
        health -= amount;
        if (health < 0) health = 0;
        System.out.println(name + " получил " + amount + " урона. Оставшееся ХП: " + health);
    }

    public void heal() {
        health = maxHealth;
        System.out.println(name + " восстановил ХП до " + maxHealth);
    }

    public boolean isAlive() {
        return health > 0;
    }

    @Override
    public String toString() {
        return name + " (ХП: " + health + "/" + maxHealth + ", Урон: " + damage +
                ", Дальность перемещения: " + movementRange + ", Дальность атаки: " + attackRange + ")";
    }

    // Метод для атаки
    public void attack(Unit target) {
        if (this.team != target.getTeam()) { // Проверка, что юнит не атакует свою команду
            int actualDamage = (int) (this.damage * (0.8 + Math.random() * 0.4)); // Урон будет варьироваться
            target.takeDamage(actualDamage);
        } else {
            System.out.println("Нельзя атаковать юнитов своей команды!");
        }
    }

    // Метод для магической атаки
    public void magicAttack(Unit target, int magicDamage) {
        if (this.team != target.getTeam()) {
            target.takeDamage(magicDamage);
            System.out.println(name + " применил магический урон " + magicDamage + " к " + target.getName());
        } else {
            System.out.println("Нельзя атаковать своих юнитов магией!");
        }
    }

    // Метод для перемещения юнита
    public void move(Position newPosition, GameMap gameMap) {
        if (gameMap.isWalkable(newPosition)) {
            this.position = newPosition;
            System.out.println(name + " переместился на " + newPosition);
        } else {
            System.out.println(name + " не может переместиться в эту клетку!");
        }
    }
}