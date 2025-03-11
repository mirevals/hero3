package org.example.game;




import java.util.ArrayList;
import java.util.List;
public class Hero {

    private final String name;  // Имя героя
    private final int maxMoves;  // Максимальное количество перемещений за ход
    private int currentMoves;  // Текущее количество оставшихся перемещений
    private Position position;  // Позиция героя на карте
    private final List<String> inventory;  // Список предметов, которые собрал герой
    private Unit unit;  // Юнит, принадлежащий герою
    private final Team team;

    // Конструктор героя
    public Hero(String name, int maxMoves, Position startPosition, Unit unit, Team team) {
        this.name = name;
        this.maxMoves = maxMoves;
        this.currentMoves = maxMoves;
        this.position = startPosition;
        this.inventory = new ArrayList<>();
        this.unit = unit;
        this.team = team;
    }

    // Метод для получения имени героя
    public String getName() {
        return name;
    }

    public Team getTeam() {
        return team;
    }

    // Метод для получения позиции героя
    public Position getPosition() {
        return position;
    }

    // Метод для получения количества оставшихся перемещений
    public int getCurrentMoves() {
        return currentMoves;
    }

    // Метод для перемещения героя
    public void move(Position newPosition) {
        if (currentMoves > 0) {
            this.position = newPosition;
            currentMoves--;  // Уменьшаем количество оставшихся перемещений
        }
    }

    // Метод для пополнения инвентаря героя
    public void addItemToInventory(String item) {
        inventory.add(item);
        System.out.println(item + " добавлен в инвентарь " + name);
    }

    // Метод для использования предмета из инвентаря
    public void useItem(String item) {
        if (inventory.contains(item)) {
            inventory.remove(item);
            System.out.println(name + " использует " + item);
        } else {
            System.out.println(item + " не найден в инвентаре " + name);
        }
    }

    // Метод для завершения хода героя (восстановление перемещений)
    public void endTurn() {
        this.currentMoves = maxMoves;
        System.out.println(name + " завершил ход. Перемещения восстановлены.");
    }

    // Метод для получения юнита героя
    public Unit getUnit() {
        return unit;
    }

    // Метод для изменения юнита героя (например, если герой потерял своего юнита)
    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public int getMaxMoves() {
        return maxMoves;
    }
}