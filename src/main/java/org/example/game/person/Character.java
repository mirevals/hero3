package org.example.game.person;

import org.example.game.Gold;
import org.example.game.build.Castle;
import org.example.game.map.Position;
import org.example.game.weapons.AttackRange;
import org.example.game.map.GameMap;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;
import java.util.UUID;

public abstract class Character implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String id;
    private String name;  // Имя героя
    private int maxMoves;  // Максимальное количество перемещений за ход
    private int currentMoves;  // Текущее количество оставшихся перемещений
    private Position position;  // Позиция героя на карте
    private final List<String> inventory;  // Список предметов, которые собрал герой
    private Team team;
    private final List<Unit> units;
    private int gold;
    private int health;
    private int attack;
    private int defense;
    private AttackRange attackRange; // Дальность атаки

    // Конструктор героя
    public Character(String name, int maxMoves, Position startPosition, Team team, int gold, int health, int attack, int defense, int attackRange, List<Unit> units) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.maxMoves = maxMoves;
        this.currentMoves = maxMoves;
        this.position = startPosition;
        this.inventory = new ArrayList<>();
        this.units = units;
        this.team = team;
        this.gold = gold;
        this.health = health;
        this.attack = attack;
        this.defense = defense;
        this.attackRange = new AttackRange(attackRange);
    }

    // Геттеры
    public String getName() { return name; }
    public int getGold() { return gold; }
    public Team getTeam() { return team; }
    public Position getPosition() { return position; }
    public int getCurrentMoves() { return currentMoves; }
    public int getMaxMoves() { return maxMoves; }
    public List<Unit> getUnits() { return units; }
    public int getHealth() { return health; }
    public int getAttack() { return attack; }
    public int getDefense() { return defense; }
    public int getAttackRange() { return attackRange.getRange(); }
    public String getId() {
        return id;
    }

    // Сеттеры
    public void setName(String name) { this.name = name; }
    public void setMaxMoves(int maxMoves) { this.maxMoves = maxMoves; }
    public void setTeam(Team team) { this.team = team; }
    public void setGold(int gold) { this.gold = gold; }
    public void setHealth(int health) { this.health = health; }
    public void setAttack(int attack) { this.attack = attack; }
    public void setDefense(int defense) { this.defense = defense; }
    public void setAttackRange(int attackRange) { this.attackRange.setRange(attackRange); }

    // Метод для установки количества оставшихся перемещений
    public void setCurrentMoves(int currentMoves) {
        if (currentMoves < 0) {
            System.out.println("Ошибка: количество перемещений не может быть меньше 0.");
        } else if (currentMoves > maxMoves) {
            System.out.println("Ошибка: количество перемещений не может быть больше " + maxMoves);
        } else {
            this.currentMoves = currentMoves;
        }
    }

    // Инвентарь
    public void addItemToInventory(String item) {
        inventory.add(item);
        System.out.println(item + " добавлен в инвентарь " + name);
    }

    public void useItem(String item) {
        if (inventory.contains(item)) {
            inventory.remove(item);
            System.out.println(name + " использует " + item);
        } else {
            System.out.println(item + " не найден в инвентаре " + name);
        }
    }

    // Ход героя
    public void endTurn() {
        this.currentMoves = maxMoves;
        System.out.println(name + " завершил ход. Перемещения восстановлены.");
    }

    // Добавление юнита
    public void addUnit(Unit unit) {
        this.units.add(unit);
        System.out.println("Герой " + name + " получил нового юнита: " + unit.getName());
    }

    // Координаты
    public int getX() { return position.getX(); }
    public int getY() { return position.getY(); }
    public void setPosition(int x, int y) { this.position = new Position(x, y); }

    public void setX(int x) {
        this.position = new Position(x, this.position.getY());
    }

    public void setY(int y) {
        this.position = new Position(this.position.getX(), y);
    }

    public void die() {
        this.health = 0;
        System.out.println(name + " умер.");
    }

    public boolean isDead() {
        return health <= 0;
    }

    public void addGold(int amount) {
        if (amount >= 0) {
            this.gold += amount;
            System.out.println(amount + " золота добавлено герою " + name + ". Текущее количество золота: " + gold);
        } else {
            System.out.println("Ошибка: количество золота должно быть положительным.");
        }
    }

    public boolean spendGold(int amount) {
        if (amount > 0 && this.gold >= amount) {
            this.gold -= amount;
            System.out.println(amount + " золота потрачено героем " + name + ". Текущее количество золота: " + gold);
            return true;
        } else if (amount <= 0) {
            System.out.println("Невозможно потратить отрицательное количество золота.");
        } else {
            System.out.println("Недостаточно золота для покупки.");
        }
        return false;
    }

    public void addMoves(int amount) {
        if (amount > 0) {
            this.currentMoves = Math.min(this.currentMoves + amount, this.maxMoves);
            System.out.println(amount + " шагов добавлено герою " + name + ". Текущие шаги: " + this.currentMoves);
        } else {
            System.out.println("Невозможно добавить отрицательное количество шагов.");
        }
    }

    public enum CharacterType {
        HERO('H'), ENEMY('A');

        private final char symbol;

        CharacterType(char symbol) {
            this.symbol = symbol;
        }

        public char getSymbol() {
            return symbol;
        }
    }

    public abstract CharacterType getType();

    // Метод для получения урона
    public void takeDamage(int damage) {
        // Вычисляем фактический урон с учетом защиты
        int finalDamage = damage;

        // Уменьшаем здоровье на полученный урон
        this.health -= finalDamage;
        // Если здоровье персонажа стало меньше или равно нулю, он умирает
        if (this.health <= 0) {
            this.die();
        }

        System.out.println(name + " получил урон: " + finalDamage + ". Текущее здоровье: " + this.health);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        // Handle the case where attackRange was an int in older versions
        try {
            // Try to read as AttackRange object (new format)
            Object obj = in.readObject();
            if (obj instanceof AttackRange) {
                this.attackRange = (AttackRange) obj;
            } else if (obj instanceof Integer) {
                // If it's an Integer, it's the old format
                this.attackRange = new AttackRange((Integer) obj);
            } else {
                // Handle unexpected type, perhaps set a default or throw an error
                this.attackRange = new AttackRange(0); // Default value or handle as an error
                System.err.println("Unexpected type for attackRange during deserialization: " + obj.getClass().getName());
            }
        } catch (IOException e) {
            // This catch is for cases where the field might not have been written as an object (e.g., if it was a primitive)
            // If we are here, it means in.readObject() failed, implying it might have been an int field
            // Try to read it as an int directly (this might not be strictly correct if the field was skipped entirely)
            // Re-throw if it's not a primitive read error, or if this is not the expected fallback.
            System.err.println("IOException during attackRange deserialization, attempting int fallback: " + e.getMessage());
            this.attackRange = new AttackRange(in.readInt()); // Fallback to reading as int
        }
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        // Custom serialization for attackRange to ensure consistent writing
        out.writeObject(this.attackRange);
    }

    public boolean canMove() {
        return this.currentMoves > 0;
    }

    public boolean isValidMove(int x, int y, GameMap gameMap) {
        // Implementation of isValidMove method
        return false; // Placeholder return, actual implementation needed
    }
}