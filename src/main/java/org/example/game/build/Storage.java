package org.example.game.build;

import org.example.game.person.Character;
import org.example.game.person.Hero;
import org.example.game.person.Team;
import java.util.List;
import java.util.Scanner;

public class Storage {
    private static final Scanner scanner = new Scanner(System.in);
    static boolean useBuilding = false;

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
            scanner.nextLine(); // очистка буфера

            if (serviceChoice == 0) {
                System.out.println("Отмена выбора услуги.");
                return false;
            }

            if (serviceChoice < 1 || serviceChoice > services.size()) {
                System.out.println("Неверный выбор услуги.");
                return false;
            }

            // Начинаем услугу с текущим героем
            if (serviceBuilding.startService(currentHero, serviceChoice - 1)) {
                System.out.println("Вы начали использовать услугу в " + serviceBuilding.getName());
                useBuilding = true;
                return true;
            } else {
                System.out.println("Не удалось начать услугу.");
                return false;
            }
        } else {
            // Для не-сервисных зданий оставляем стандартное поведение
            useBuilding = true;
            System.out.println("Вы используете здание " + selectedBuilding.getName());
            return true;
        }
    }

    private static String formatDuration(int minutes) {
        int days = minutes / (24 * 60);
        int hours = (minutes % (24 * 60)) / 60;
        int mins = minutes % 60;
        
        StringBuilder result = new StringBuilder();
        if (days > 0) {
            result.append(days).append(" дн. ");
        }
        if (hours > 0) {
            result.append(hours).append(" ч. ");
        }
        if (mins > 0 || (days == 0 && hours == 0)) {
            result.append(mins).append(" мин.");
        }
        return result.toString().trim();
    }
}