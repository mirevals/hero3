package org.example.game.build;

import org.example.game.person.Character;
import org.example.game.map.Position;

import java.util.ArrayList;
import java.util.List;

public class Castle {

    private final String name;  // Название замка
    private static Position position = null;  // Позиция замка на карте
    private final List<Building> buildings;  // Список строений в замке
    private boolean isCaptured;  // Флаг для проверки, захвачен ли замок

    // Конструктор замка
    public Castle(String name, Position position) {
        this.name = name;
        this.position = position;
        this.buildings = new ArrayList<>();
        this.isCaptured = false;
    }

    // Метод для получения имени замка
    public String getName() {
        return name;
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

    // Метод для захвата замка
    public void capture() {
        if (!isCaptured) {
            isCaptured = true;
            System.out.println(name + " был захвачен!");
        } else {
            System.out.println(name + " уже захвачен!");
        }
    }

    // Метод для отображения информации о замке
    public void displayCastleInfo() {
        System.out.println("Замок: " + name);
        System.out.println("Позиция: " + position);
        System.out.println("Строения в замке:");
        if (buildings.isEmpty()) {
            System.out.println("Нет строений.");
        } else {
            for (Building building : buildings) {
                System.out.println("- " + building.getName());
            }
        }
    }

    // Метод для взаимодействия с замком
    public void interactWithCastle(Character character) {
        if (isCaptured) {
            System.out.println("Замок уже захвачен, нельзя взаимодействовать.");
            return;
        }

        System.out.println("Вы можете взаимодействовать с замком: ");
        displayCastleInfo();
        System.out.println("Выберите строение для взаимодействия (по индексу):");
        // Вставить логику взаимодействия с конкретным строением
        // Например, можно позволить пользователю выбрать строение для взаимодействия
    }

    // Проверка на захваченный ли замок
    public boolean isCaptured() {
        return isCaptured;
    }

    public static Position getPosition(){
        return position;
    }


}