package org.example.game.build;

import org.example.game.person.Character;
import org.example.game.person.Hero;
import org.example.game.person.Team;
import java.util.List;
import java.util.Scanner;
import java.util.ArrayList;

public class Storage {
    private static final Scanner scanner = new Scanner(System.in);
    static boolean useBuilding = false;

    public static Building findBuildingByInput(String input, List<Building> buildings) {
        // Try to parse as number first
        try {
            int index = Integer.parseInt(input.trim());
            if (index > 0 && index <= buildings.size()) {
                return buildings.get(index - 1);
            }
        } catch (NumberFormatException e) {
            // Not a number, try to find by name
            String searchTerm = input.trim().toLowerCase();
            List<Building> matches = new ArrayList<>();
            
            for (Building building : buildings) {
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

    // Метод для использования здания на основе выбранного номера здания и типа замка
    public static boolean useBuilding(int buildingChoice, Castle castle, Hero currentHero) {
        List<Building> buildings = castle.getConstructedBuildings();
        if (buildingChoice < 1 || buildingChoice > buildings.size()) {
            System.out.println("Неверный выбор здания.");
            return false;
        }

        Building selectedBuilding = buildings.get(buildingChoice - 1);
        
        if (selectedBuilding instanceof ServiceBuilding) {
            ServiceBuilding serviceBuilding = (ServiceBuilding) selectedBuilding;
            
            // Проверяем, может ли здание принять посетителя
            if (!serviceBuilding.canAcceptVisitor()) {
                System.out.println("Извините, " + serviceBuilding.getName() + " сейчас заполнен.");
                return false;
            }

            // Проверяем, есть ли у героя юниты
            if (currentHero == null || currentHero.getUnits().isEmpty()) {
                System.out.println("У вас нет юнитов для использования услуг.");
                return false;
            }

            // Получаем список доступных услуг
            List<ServiceBuilding.Service> services = serviceBuilding.getAvailableServices();
            if (services.isEmpty()) {
                System.out.println("В " + serviceBuilding.getName() + " нет доступных услуг.");
                return false;
            }

            // Показываем меню услуг
            System.out.println("\nДоступные услуги в " + serviceBuilding.getName() + ":");
            for (int i = 0; i < services.size(); i++) {
                ServiceBuilding.Service service = services.get(i);
                System.out.println((i + 1) + ". " + service.getName() + 
                                 " - Длительность: " + formatDuration(service.getDurationMinutes()) + 
                                 ", Стоимость: " + service.getGoldCost() + " золота");
            }

            // Выбор услуги
            System.out.print("\nВыберите услугу (или 0 для отмены): ");
            int serviceChoice = scanner.nextInt();
            scanner.nextLine();

            if (serviceChoice == 0) {
                System.out.println("Отмена выбора услуги.");
                return false;
            }

            if (serviceChoice < 1 || serviceChoice > services.size()) {
                System.out.println("Неверный выбор услуги.");
                return false;
            }

            ServiceBuilding.Service selectedService = services.get(serviceChoice - 1);
            serviceBuilding.provideService(currentHero, selectedService);
            return true;
        }
        return false;
    }

    private static String formatDuration(int minutes) {
        int hours = minutes / 60;
        int mins = minutes % 60;
        if (hours > 0) {
            return String.format("%dч %02dм", hours, mins);
        }
        return String.format("%dм", mins);
    }
}