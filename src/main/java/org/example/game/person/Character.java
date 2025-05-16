package org.example.game.person;

import org.example.game.Gold;
import org.example.game.build.Castle;
import org.example.game.map.Position;

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

public abstract class Character implements Serializable {

    private static final long serialVersionUID = 1L;

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
    private int attackRange; // Дальность атаки

    // Конструктор героя
    public Character(String name, int maxMoves, Position startPosition, Team team, int gold, int health, int attack, int defense, int attackRange, List<Unit> units) {
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
        this.attackRange = attackRange;
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
    public int getAttackRange() { return attackRange; }

    // Сеттеры
    public void setName(String name) { this.name = name; }
    public void setMaxMoves(int maxMoves) { this.maxMoves = maxMoves; }
    public void setTeam(Team team) { this.team = team; }
    public void setGold(int gold) { this.gold = gold; }
    public void setHealth(int health) { this.health = health; }
    public void setAttack(int attack) { this.attack = attack; }
    public void setDefense(int defense) { this.defense = defense; }
    public void setAttackRange(int attackRange) { this.attackRange = attackRange; }



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
    public enum CharacterType {
        HERO, ENEMY
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
}