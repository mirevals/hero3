package org.example.game.build;

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
        System.out.println("\nВы можете выбрать здание по номеру или ввести часть названия здания.");
    }

    public static Building findBuildingByInput(String input) {
        // Try to parse as number first
        try {
            int index = Integer.parseInt(input.trim());
            if (index > 0 && index <= availableBuildings.size()) {
                return availableBuildings.get(index - 1);
            }
        } catch (NumberFormatException e) {
            // Not a number, try to find by name
            String searchTerm = input.trim().toLowerCase();
            List<Building> matches = new ArrayList<>();
            
            for (Building building : availableBuildings) {
                if (building.getName().toLowerCase().contains(searchTerm)) {
                    matches.add(building);
                }
            }
            
            if (matches.size() == 1) {
                return matches.get(0);
            } else if (matches.size() > 1) {
                System.out.println("Найдено несколько зданий, соответствующих вашему запросу:");
                for (int i = 0; i < matches.size(); i++) {
                    System.out.println((i + 1) + ". " + matches.get(i).getName());
                }
                System.out.println("Пожалуйста, уточните выбор, введя более точное название или номер.");
                return null;
            }
        }
        return null;
    }
}