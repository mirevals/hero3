package org.example.game.build;

import org.example.App;
import org.example.game.Player;
import org.example.game.build.GuardPost;
import org.example.game.build.Building;
import org.example.game.build.Hotel;
import org.example.game.build.Cafe;
import org.example.game.build.BarberShop;

import java.util.ArrayList;
import java.util.List;

public class Shop {
    // Статический список зданий, доступных для покупки
    public static List<Building> availableBuildings;

    static {
        availableBuildings = new ArrayList<>();
        availableBuildings.add(new GuardPost());
        availableBuildings.add(new Tavern());
        availableBuildings.add(new Hotel());
        availableBuildings.add(new Cafe());
        availableBuildings.add(new BarberShop());
    }

    public static void showAvailableBuildings() {
        System.out.println("Доступные здания для покупки:");
        for (int i = 0; i < availableBuildings.size(); i++) {
            Building building = availableBuildings.get(i);
            System.out.println((i + 1) + ". " + building.getName());
        }
    }
}