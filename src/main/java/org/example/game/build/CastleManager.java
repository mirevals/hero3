package org.example.game.build;

import org.example.game.person.Character;
import org.example.game.map.GameMap;
import org.example.game.person.Hero;
import org.example.game.person.Team;

import java.util.Scanner;

public class CastleManager {

    private final GameMap gameMap;  // Ссылка на объект GameMap
    public static boolean isInCastle = false;  // Флаг, находится ли герой в замке
    private final String castleType;  // Тип замка (геройский или противника)
    private final Shop shop;  // Магазин, который доступен в замке
    private final Castle currentCastle; // Теперь знаем, с каким замком работаем



    public CastleManager(GameMap gameMap, Castle currentCastle) {
        this.gameMap = gameMap;
        this.castleType = currentCastle.getType();
        this.shop = new Shop();
        this.currentCastle = currentCastle;

    }

    // Метод для обработки команд в замке
    public void processCastleCommands() {
        // Цикл для нахождения в замке
        while (isInCastle) {
            System.out.println("Вы в замке. Доступные команды:");
            System.out.println("q - выйти из замка");
            System.out.println("hh - помощь (список команд)");
            System.out.println("v - взаимодействовать с NPC");
            System.out.println("h - выбрать героя");
            System.out.println("m - открыть магазин");
            System.out.println("b - показать список построек");


            // Ввод команды
            Scanner scanner = new Scanner(System.in);
            String command = scanner.nextLine().trim().toLowerCase();

            switch (command) {
                case "q":
                    gameMap.exitCastle();  // Выход из замка через GameMap
                    // После выхода из замка печатаем карту и завершаем цикл
                    return;  // Прерываем цикл, так как мы больше не в замке
                case "hh":
                    showHelp();  // Показать список команд
                    break;
                case "h":
                    chooseHero();  // Взаимодействие с NPC
                    break;
                case "m":
                    openShop(character);  // Открытие магазина
                case "b":
                    currentCastle.showBuildings();
                    break;
                default:
                    System.out.println("Неизвестная команда. Введите 'h' для справки.");
            }
        }
    }

    // Открыть магазин
    private void openShop(Character character) {
        System.out.println("Добро пожаловать в магазин!");
        shop.showAvailableBuildings();  // Показать доступные здания

        // Возможность покупать здания
        System.out.println("Введите название здания, которое вы хотите купить:");
        Scanner scanner = new Scanner(System.in);
        String buildingName = scanner.nextLine().trim();

        Building purchasedBuilding = shop.buyBuilding(buildingName); // Покупка здания


        if (purchasedBuilding != null) {
            currentCastle.addBuilding(purchasedBuilding); // Добавляем в замок
        }
    }



    // Показать помощь по командам
    private void showHelp() {
        System.out.println("Команды:");
        System.out.println("q - выйти из замка");
        System.out.println("h - показать список команд");
        System.out.println("v - взаимодействовать с NPC");
    }




    // Метод для выбора героя
    private void chooseHero() {
        System.out.println("Выберите героя:");
        System.out.println("1 - Герой 1");
        System.out.println("2 - Герой 2");
        System.out.println("3 - Герой 3");

        // Считываем ввод пользователя
        Scanner scanner = new Scanner(System.in);
        int heroChoice = scanner.nextInt();

        Hero selectedHero = null;

        switch (heroChoice) {
            case 1:
                selectedHero = new Hero("Герой 1", 5, Team.HERO, 100);  // Пример создания героя
                System.out.println("Вы выбрали Героя 1.");
                break;
            case 2:
                selectedHero = new Hero("Герой 2", 4, Team.HERO, 80);   // Пример создания героя
                System.out.println("Вы выбрали Героя 2.");
                break;
            case 3:
                selectedHero = new Hero("Герой 3", 6, Team.HERO, 120);  // Пример создания героя
                System.out.println("Вы выбрали Героя 3.");
                break;
            default:
                System.out.println("Некорректный выбор. Пожалуйста, выберите номер героя из списка.");
                return;
        }

        // Здесь можно добавить логику для добавления выбранного героя в команду или в другие части игры
        // Например, добавляем выбранного героя в команду
        System.out.println("Герой " + selectedHero.getName() + " был успешно выбран.");

        processCastleCommands();
    }

    // Включаем замок, флаг для того, чтобы начать обработку команд
    public static void enterCastle(GameMap gameMap, Castle currentCastle) {
        // Создаем объект CastleManager с типом замка
        CastleManager castleManager = new CastleManager(gameMap, currentCastle);

        // Устанавливаем флаг нахождения в замке
        castleManager.isInCastle = true;
        System.out.println("Вы вошли в " + (castleType == 'C' ? "замок героя!" : "замок противника!"));

        // Запуск обработки команд
        castleManager.processCastleCommands();
    }
}