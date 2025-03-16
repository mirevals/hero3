package org.example.game.person;




import org.example.game.Gold;
import org.example.game.map.Position;

import java.util.ArrayList;
import java.util.List;

public class Character {

    private final String name;  // Имя героя
    private final int maxMoves;  // Максимальное количество перемещений за ход
    private int currentMoves;  // Текущее количество оставшихся перемещений
    Position position;  // Позиция героя на карте
    private final List<String> inventory;  // Список предметов, которые собрал герой
    private final Team team;
    private final List<Unit> units;
    private int gold;

    // Конструктор героя
    public Character(String name, int maxMoves, Position startPosition, Team team, int gold) {
        this.name = name;
        this.maxMoves = maxMoves;
        this.currentMoves = maxMoves;
        this.position = startPosition;
        this.inventory = new ArrayList<>();
        this.units = new ArrayList<>();
        this.team = team;
        this.gold = gold;
    }

    // Метод для получения имени героя
    public String getName() {
        return name;
    }

    public int getGold() {
        return gold;
    }

    public void setGold (int gold){
        this.gold = gold;
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

    public int getMaxMoves() {
        return maxMoves;
    }

    public List<Unit> getUnits() {
        return units;
    }

    // Метод для добавления юнита к герою
    public void addUnit(Unit unit) {
        this.units.add(unit);
        System.out.println("Герой " + name + " получил нового юнита: " + unit.getName());
    }

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

    // Получение координаты X героя
    public int getX() {
        return position.getX();  // Предполагаем, что в классе Position есть метод getX()
    }

    // Получение координаты Y героя
    public int getY() {
        return position.getY();  // Предполагаем, что в классе Position есть метод getY()
    }

    public void setPosition(int x, int y) {
        this.position = new Position(x, y);  // Создаем новый объект Position и присваиваем его позиции героя
    }
}