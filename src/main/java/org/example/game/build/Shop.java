package org.example.game.build;

import org.example.App;
import org.example.game.Player;
import org.example.game.build.GuardPost;
import org.example.game.build.Building;

import java.util.ArrayList;
import java.util.List;

public class Shop {
    // Статический список зданий, доступных для покупки
    public static List<Building> availableBuildings;

    static {
        availableBuildings = new ArrayList<>();
        availableBuildings.add(new GuardPost());
        availableBuildings.add(new Tavern());
    }

    public static void showAvailableBuildings() {
        System.out.println("Доступные здания для покупки:");
        for (int i = 0; i < availableBuildings.size(); i++) {
            Building building = availableBuildings.get(i);
            System.out.println((i + 1) + ". " + building.getName());
        }
    }
}