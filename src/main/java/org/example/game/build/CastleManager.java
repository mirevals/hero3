package org.example.game.build;

import org.example.game.person.Character;
import org.example.game.map.GameMap;

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
    public void processCastleCommands(Character character) {
        // Цикл для нахождения в замке
        while (isInCastle) {
            System.out.println("Вы в замке. Доступные команды:");
            System.out.println("q - выйти из замка");
            System.out.println("h - помощь (список команд)");
            System.out.println("v - взаимодействовать с NPC");
            System.out.println("m - открыть магазин");
            System.out.println("b - показать список построек");

            // Ввод команды
            Scanner scanner = new Scanner(System.in);
            String command = scanner.nextLine().trim().toLowerCase();

            switch (command) {
                case "q":
                    gameMap.exitCastle(character);  // Выход из замка через GameMap
                    // После выхода из замка печатаем карту и завершаем цикл
                    return;  // Прерываем цикл, так как мы больше не в замке
                case "h":
                    showHelp();  // Показать список команд
                    break;
                case "v":
                    interactWithNPC();  // Взаимодействие с NPC
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

    // Показать помощь по командам
    private void showHelp() {
        System.out.println("Команды:");
        System.out.println("q - выйти из замка");
        System.out.println("h - показать список команд");
        System.out.println("v - взаимодействовать с NPC");
        System.out.println("m - открыть магазин");
        System.out.println("b - показать постройки");
    }

    // Взаимодействие с NPC
    private void interactWithNPC() {
        System.out.println("Вы встретили NPC!");
        System.out.println("Что вы хотите сделать?");
        System.out.println("1 - Поговорить с NPC");
        System.out.println("2 - Купить что-то у NPC");
        System.out.println("3 - Выйти из разговора");

        Scanner scanner = new Scanner(System.in);
        int npcChoice = scanner.nextInt();

        switch (npcChoice) {
            case 1:
                System.out.println("Вы поговорили с NPC.");
                break;
            case 2:
                System.out.println("Вы купили что-то у NPC.");
                break;
            case 3:
                System.out.println("Вы покинули разговор.");
                break;
            default:
                System.out.println("Некорректный выбор.");
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


    // Включаем замок, флаг для того, чтобы начать обработку команд
    public static void enterCastle(Character character, GameMap gameMap, Castle currentCastle) {
        // Создаем объект CastleManager с типом замка
        CastleManager castleManager = new CastleManager(gameMap, currentCastle);

        // Устанавливаем флаг нахождения в замке
        castleManager.isInCastle = true;
        System.out.println("Вы вошли в " + currentCastle.getName());

        // Запуск обработки команд
        castleManager.processCastleCommands(character);
    }
}