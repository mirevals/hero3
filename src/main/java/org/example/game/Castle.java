package org.example.game;



import java.util.ArrayList;
import java.util.List;

public class Castle {

    private final String name;  // Название замка
    private final Position position;  // Позиция замка на карте
    private final List<Building> buildings;  // Список строений в замке

    // Конструктор замка
    public Castle(String name, Position position) {
        this.name = name;
        this.position = position;
        this.buildings = new ArrayList<>();
    }

    // Метод для получения имени замка
    public String getName() {
        return name;
    }

    // Метод для получения позиции замка
    public Position getPosition() {
        return position;
    }

    // Метод для добавления строения в замок
    public void addBuilding(Building building) {
        buildings.add(building);
        System.out.println("Добавлено строение: " + building.getName());
    }

    // Метод для получения списка всех строений в замке
    public List<Building> getBuildings() {
        return buildings;
    }

    // Метод для захвата замка (пример: можно добавить дополнительные условия для захвата)
    public void capture() {
        System.out.println(name + " был захвачен!");
    }
}