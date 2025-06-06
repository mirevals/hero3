package org.example.game.person;

import org.example.game.map.Position;
import java.io.Serializable;

public class Unit implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String name;  // Имя юнита
    private int health;  // Здоровье юнита
    private final int damage;  // Урон, который наносит юнит
    private final int moveRange;  // Дальность перемещения
    private final int attackRange;  // Дальность атаки
    private final Team team;  // Команда, к которой принадлежит юнит
    private int stackSize;  // Количество юнитов в стеке
    private Position position;  // Позиция юнита на карте
    private char symbol;
    private final int cost;

    public enum UnitType implements Serializable {
        WARRIOR,
        ARCHER,
        SPEARMAN,
        MAGE;  // Пример нескольких типов юнитов
    }

    private final UnitType unitType;  // Тип юнита

    // Конструктор юнита
    public Unit(UnitType unitType, int health, int damage, int moveRange, int attackRange, Team team, char symbol, int cost) {
        this.unitType = unitType;
        this.name = unitType.name();  // Имя юнита по умолчанию — это его тип
        this.health = health;
        this.damage = damage;
        this.moveRange = moveRange;
        this.attackRange = attackRange;
        this.team = team;
        this.stackSize = 1;  // Изначально один юнит в стеке
        this.position = position != null ? position : new Position(0, 0);  // Используем переданную позицию или (0, 0) по умолчанию
        this.symbol = symbol;
        this.cost = cost;
    }

    // Метод для установки позиции юнита
    public void setPosition(Position position) {
        this.position = position;
    }

    // Метод для получения общего здоровья юнитов в стеке
    public int getTotalHealth() {
        return health * stackSize;
    }

    // Метод для получения общего урона юнитов в стеке
    public int getTotalDamage() {
        return damage * stackSize;
    }

    // Метод для перемещения юнита
    public void move() {
        // Логика перемещения
        System.out.println(name + " перемещается на " + moveRange + " клеток.");
    }

    // Получение дефолтного символа по типу юнита
    private String getDefaultSymbol() {
        switch (unitType) {
            case WARRIOR: return "W";  // Воин
            case ARCHER: return "A";   // Лучник
            case MAGE: return "M";     // Маг
            default: return "?";       // Если тип неизвестен
        }
    }

    // Метод для получения символа юнита
    public char getSymbol() {
        return symbol;
    }

    // Геттеры для полей
    public String getName() {
        return name;
    }

    public int getHealth() {
        return health;
    }

    // Метод для получения команды юнита
    public Team getTeam() {
        return team;
    }

    // Метод для получения дальности перемещения
    public int getMoveRange() {
        return moveRange;
    }

    // Метод для получения дальности атаки
    public int getAttackRange() {
        return attackRange;
    }

    // Метод для получения количества юнитов в стеке
    public int getStackSize() {
        return stackSize;
    }

    // Метод для увеличения количества юнитов в стеке
    public void increaseStack(int amount) {
        stackSize += amount;
    }

    // Метод для получения типа юнита
    public UnitType getUnitType() {
        return unitType;
    }

    // Метод для получения позиции юнита
    public Position getPosition() {
        return position;
    }

    public void attack(Unit target) {
        // Проверка, что цель не принадлежит той же команде
        if (this.team == target.getTeam()) {
            System.out.println(name + " не может атаковать юнитов своей команды.");
            return;  // Прекращаем выполнение метода, если цель из той же команды
        }

        // Логика атаки
        System.out.println(name + " атакует " + target.getName() + " с урона: " + getTotalDamage());
        // Уменьшаем здоровье цели
        target.takeDamage(getTotalDamage());
    }

    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            die();  // Вызов метода die
        } else {
            System.out.println(name + " получил " + damage + " урона, осталось здоровья: " + health);
        }
    }

    // Метод для объединения юнитов в стеке
    public void mergeStacks(Unit unit) {
        if (this.unitType == unit.getUnitType()) {
            this.stackSize += unit.getStackSize();  // Объединение стеков
            unit.stackSize = 0;  // Очистка стека у другого юнита
            System.out.println(name + " объединился с " + unit.getName() + ". Новый размер стека: " + stackSize);
        } else {
            System.out.println("Невозможно объединить юнитов разных типов.");
        }
    }

    public UnitType getType() {
        return this.unitType;
    }

    public int getCost() {
        return this.cost;
    }

    public void die() {
        System.out.println(name + " погиб.");
        this.stackSize = 0;
        this.health = 0;
    }

    public boolean isDead() {
        return health <= 0 || stackSize <= 0;
    }
}