package org.example.game.build;

import org.example.game.person.Character;
import org.example.game.map.Position;

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

public abstract class Castle implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Building> constructedBuildings;
    private final String name;  // Название замка
    private Position position = null;  // Позиция замка на карте
    private boolean isCaptured;  // Флаг для проверки, захвачен ли замок

    public void setPosition(Position position) {
        this.position = position;
    }


    public enum CastleType {
        HERO, ENEMY
    }

    // Конструктор замка
    public Castle(String name, Position position) {
        this.name = name;
        this.position = position;
        this.isCaptured = false;
        constructedBuildings = new ArrayList<>();
    }



    public Position getPosition(){
        return position;
    }

    // Метод для получения имени замка
    public String getName() {
        return name;
    }



    public abstract CastleType getType();


    public void showConstructedBuildings() {
        if (constructedBuildings.isEmpty()) {
            System.out.println("Нет построек.");
        } else {
            int index = 1; // Начинаем с 1
            for (Building building : constructedBuildings) {
                System.out.println(index + ". " + building.getName());  // Выводим номер и название здания
                index++;  // Увеличиваем номер для следующего здания
            }
        }
    }


    public void addBuilding(Building building) {
        constructedBuildings.add(building);
    }

    // Получить список построек
    public List<Building> getConstructedBuildings() {
        return constructedBuildings;
    }

    public int getX() {
        return position.getX();
    }

    public int getY() {
        return position.getY();
    }


}