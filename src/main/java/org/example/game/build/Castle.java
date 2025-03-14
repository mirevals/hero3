package org.example.game.build;

import org.example.game.person.Character;
import org.example.game.map.Position;

import java.util.ArrayList;
import java.util.List;

public abstract class Castle {

    private final String name;  // Название замка
    private Position position = null;  // Позиция замка на карте
    private final List<Building> buildings;  // Список строений в замке
    private boolean isCaptured;  // Флаг для проверки, захвачен ли замок


    // Конструктор замка
    public Castle(String name, Position position) {
        this.name = name;
        this.position = position;
        this.buildings = new ArrayList<>();
        this.isCaptured = false;
    }



    public Position getPosition(){
        return position;
    }

    // Метод для получения имени замка
    public String getName() {
        return name;
    }

    // Метод добавления и получения списка построек
    public void addBuilding(Building building) {
        buildings.add(building);
        System.out.println("В " + name + " добавлено строение: " + building.getName());
    }

    public void showBuildings() {
        System.out.println("Постройки в " + name + ":");
        if (buildings.isEmpty()) {
            System.out.println("Нет построек.");
        } else {
            for (Building building : buildings) {
                System.out.println("- " + building.getName());
            }
        }
    }

    public abstract String getType();



}