package org.example.game.build;

import org.example.game.build.GuardPost;
import org.example.game.build.Building;

import java.util.ArrayList;
import java.util.List;

public class Shop {
    private List<Building> availableBuildings;

    public Shop() {
        availableBuildings = new ArrayList<>();
        availableBuildings.add(new GuardPost()); // Добавляем сторожевой пост в магазин
    }

    public void showAvailableBuildings() {
        System.out.println("Доступные здания для покупки:");
        for (Building building : availableBuildings) {
            System.out.println("- " + building.getName());
        }
    }

    public Building buyBuilding(String buildingName) {
        for (Building building : availableBuildings) {
            if (building.getName().equalsIgnoreCase(buildingName)) {
                System.out.println("Вы купили здание: " + building.getName());
                return building;
            }
        }
        System.out.println("Здание с именем " + buildingName + " не найдено.");
        return null;
    }
}