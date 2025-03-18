package org.example.game.build;

import org.example.game.Player;
import org.example.game.map.MapManager;
import org.example.game.map.Position;
import org.example.game.person.Character;
import org.example.game.map.GameMap;
import org.example.game.person.Enemy;
import org.example.game.person.Hero;
import org.example.game.person.Team;

import java.util.Scanner;

import static org.example.game.build.Shop.availableBuildings;

public class CastleManager {
    public static boolean isInCastle;  // флаг, показывающий, находимся ли мы в замке
    private static boolean isFirstExit = true;
    private static boolean isFirstHero = true;
    static boolean isTavernNotBuild = true;

    private static Scanner scanner = new Scanner(System.in);  // Один Scanner на всю программу


    public static void processCastleCommands(Castle castle, Hero hero, Enemy enemy, Player player, EnemyCastle enemyCastle, HeroCastle heroCastle, GameMap gameMap, MapManager mapManager) {
        while (isInCastle) {
            if (isTavernNotBuild) {
                handleTavernNotBuilt(castle);
            } else if (isFirstHero) {
                handleFirstHero(castle, hero, player);
            } else {
                showCastleCommands();
                String command = scanner.nextLine().trim().toLowerCase();
                processCastleAction(command, castle, enemy, hero, player, enemyCastle, heroCastle ,gameMap, mapManager);
            }
        }
    }

    private static void handleTavernNotBuilt(Castle castle) {
        System.out.println("Вы должны купить таверну перед тем, как выйти из замка.");
        System.out.println("Введите 'm' для входа в магазин.");
        String command = scanner.nextLine().trim().toLowerCase();

        if (command.equals("m")) {
            openShop(scanner, castle);  // Открываем магазин для покупки Таверны
        } else {
            System.out.println("Пожалуйста, купите Таверну перед тем, как продолжить.");
        }
    }

    private static void handleFirstHero(Castle castle, Hero hero, Player player) {
        System.out.println("Вы должны купить героя в Таверне перед тем, как выйти из замка.");
        System.out.println("Введите 'b' для входа в список построек.");
        String command = scanner.nextLine().trim().toLowerCase();

        if (command.equals("b")) {
            openStorage(scanner, castle, hero, player);  // Покупка героя
        } else {
            System.out.println("Пожалуйста, выберите героя перед тем, как продолжить.");
        }
    }

    private static void showCastleCommands() {
        System.out.println("Вы в замке. Доступные команды:");
        System.out.println("q - выйти из замка");
        System.out.println("hh - помощь (список команд)");
        System.out.println("v - взаимодействовать с NPC");
        System.out.println("h - выбрать героя");
        System.out.println("m - открыть магазин");
        System.out.println("b - показать список построек");
    }

    private static void processCastleAction(String command, Castle castle, Enemy enemy, Hero hero, Player player, EnemyCastle enemyCastle, HeroCastle heroCastle, GameMap gameMap, MapManager mapManager) {
        switch (command) {
            case "q":
                exitCastle(enemy, enemyCastle, heroCastle, hero, player, gameMap, castle, mapManager);  // Выход из замка
                break;
            case "m":
                openShop(scanner, castle);
                break;
            case "b":
                openStorage(scanner, castle, hero, player);
                break;
            default:
                System.out.println("Неизвестная команда. Введите 'h' для справки.");
        }
    }

    private static void openShop(Scanner scanner, Castle castle) {
        System.out.println("Добро пожаловать в магазин!");
        Shop.showAvailableBuildings();

        System.out.println("Введите номер здания, которое вы хотите купить:");
        int buildingChoice = scanner.nextInt();
        scanner.nextLine();

        Building purchasedBuilding = buyBuilding(buildingChoice, castle);

        if (purchasedBuilding != null) {
            System.out.println("Здание " + purchasedBuilding.getName() + " добавлено в ваш замок.");
        }
    }

    public static Building buyBuilding(int buildingChoice, Castle castle) {
        // Проверка, что выбор здания корректен
        if (buildingChoice > 0 && buildingChoice <= availableBuildings.size()) {
            Building building = availableBuildings.get(buildingChoice - 1);
            System.out.println("Вы купили здание: " + building.getName());

            // Добавляем здание в замок
            castle.addBuilding(building);

            // Показываем список построенных зданий в замке
            System.out.println("Список построек после добавления:");
            castle.showConstructedBuildings(); // Метод для отображения построенных зданий

            // Если куплен Tavern, обновляем соответствующий флаг
            if (building instanceof Tavern) {
                CastleManager.isTavernNotBuild = false;
                System.out.println("Таверна была построена.");
                isTavernNotBuild = false;
            }

            return building; // Возвращаем купленное здание
        } else {
            // Если выбор неверен, выводим сообщение
            System.out.println("Неверный выбор здания.");
            return null; // Возвращаем null, если выбор был неверным
        }
    }

    private static void openStorage(Scanner scanner, Castle castle, Hero hero, Player player) {
        System.out.println("Добро пожаловать в список зданий!");

        castle.showConstructedBuildings();

        System.out.println("Введите номер здания, которое вы хотите использовать:");
        int buildingChoice = scanner.nextInt();
        scanner.nextLine();

        // Проверка на допустимость выбора
        if (buildingChoice < 1) {
            System.out.println("Ошибка: неверный выбор здания.");
        } else {
            // Используем здание
            if (buildingChoice == 1) {  // Если выбрали Таверну (предположим, она под номером 1)
                openTavern(scanner, hero, player );
            } else {
                Storage.useBuilding(buildingChoice, castle);
                if (Storage.useBuilding) {
                    System.out.println("Вы успешно использовали здание.");
                } else {
                    System.out.println("Ошибка: не удалось использовать здание.");
                }
            }
        }
    }

    private static void openTavern(Scanner scanner, Hero hero, Player player) {
        System.out.println("Добро пожаловать в таверну!");

        Tavern.showTavernInfo();

        System.out.println("Введите номер героя, которого хотите купить:");
        int heroChoice = scanner.nextInt();
        scanner.nextLine();

        if (Tavern.buyHero(heroChoice, hero, player)) {
            if (hero != null) {
                System.out.println("Герой " + hero.getName() + " был успешно создан.");
                isFirstHero = false;
            }
        } else {
            System.out.println("Не удалось купить героя.");
        }
    }



    public static void enterCastle(Castle castle, Hero hero, Player player, Enemy enemy, EnemyCastle enemyCastle, HeroCastle heroCastle, GameMap gameMap, MapManager mapManager) {
        isInCastle = true;
        String castleName = (castle.getType() == Castle.CastleType.HERO) ? "замок героя!" : "замок противника!";
        System.out.println("Вы вошли в " + castleName);

        // Проверка на null
        if (castle == null) {
            System.out.println("Ошибка: замок не был инициализирован.");
            return;
        }

        processCastleCommands(castle, hero, enemy, player,enemyCastle, heroCastle, gameMap, mapManager);
    }

    public static void exitCastle(Enemy enemy, EnemyCastle enemyCastle, HeroCastle heroCastle, Hero hero, Player player, GameMap gameMap, Castle castle, MapManager mapManager) {
        if (!isInCastle) {
            System.out.println("Вы не находитесь в замке.");
            return;
        }

        Position castlePos = castle.getPosition();
        char castleSymbol = (castle.getType() == Castle.CastleType.HERO) ? 'C' : 'E';
        char castleSymbolenemy = 'E';

        char heroSymbol = 'H';

        char[][] map = gameMap.getMap();

        // Определяем новую позицию для выхода (всегда вниз)
        int newX = castlePos.getX() + 1;
        int newY = castlePos.getY();

        if (mapManager.isWalkable(newX, newY, gameMap)) {
            map[newY][newX] = heroSymbol;
            hero.setPosition(newX, newY);
            gameMap.setCellValue(newX, newY, heroSymbol);
            System.out.println("Вы покинули " + (castle.getType() == Castle.CastleType.HERO ? "замок героя" : "замок противника") + " и переместились на клетку ниже.");
        } else {
            System.out.println("Вы не можете покинуть замок, нет свободной клетки ниже.");
            return;
        }

        // Восстанавливаем символ замка
        map[castlePos.getY()][castlePos.getX()] = castleSymbol;
        map[enemyCastle.getPosition().getY()][enemyCastle.getPosition().getX()] = castleSymbolenemy;

        if (isFirstExit) {
            mapManager.startGame(hero, enemy, heroCastle, player, enemyCastle, heroCastle, gameMap, mapManager);
            isFirstExit = false;
        } else {
            gameMap.printMap();
            isInCastle = false;
        }
    }

}