package org.example.game.build;

import org.example.game.map.MapManager;
import org.example.game.person.Character;
import org.example.game.map.GameMap;
import org.example.game.person.Enemy;
import org.example.game.person.Hero;
import org.example.game.person.Team;

import java.util.Scanner;
public class CastleManager {
    private final GameMap gameMap;
    public static boolean isInCastle;  // флаг, показывающий, находимся ли мы в замке
    private final char castleType;
    private final Shop shop;
    private static Hero selectedHero;
    private static boolean isFirstExit = true;
    static Enemy enemy;
    private static Scanner scanner = new Scanner(System.in);  // Один Scanner на всю программу

    MapManager mapManager;

    public CastleManager(GameMap gameMap, char castleType) {
        this.gameMap = gameMap;
        this.mapManager = new MapManager(gameMap.getWidth(), gameMap.getHeight());
        this.castleType = castleType;
        this.shop = new Shop();
        enemy = new Enemy("Враг", 5, Team.ENEMY, 100, gameMap.getWidth(), gameMap.getHeight());
    }

    public void processCastleCommands() {
        while (isInCastle) {
            System.out.println("Вы в замке. Доступные команды:");
            System.out.println("q - выйти из замка");
            System.out.println("hh - помощь (список команд)");
            System.out.println("v - взаимодействовать с NPC");
            System.out.println("h - выбрать героя");
            System.out.println("m - открыть магазин");
            System.out.println("b - показать список построек");

            String command = scanner.nextLine().trim().toLowerCase();

            switch (command) {
                case "q":
                    exitCastle();
                    return;
                case "h":
                    chooseHero(scanner);
                    break;
                case "m":
                    openShop();
                    break;
                default:
                    System.out.println("Неизвестная команда. Введите 'h' для справки.");
            }
        }
    }

    private void openShop() {
        System.out.println("Добро пожаловать в магазин!");
        shop.showAvailableBuildings();

        System.out.println("Введите название здания, которое вы хотите купить:");
        String buildingName = scanner.nextLine().trim();

        Building purchasedBuilding = shop.buyBuilding(buildingName);

        if (purchasedBuilding != null) {
            // Добавление здания в замок
        }
    }

    private void chooseHero(Scanner scanner) {
        if (selectedHero != null) {
            System.out.println("Герой уже выбран: " + selectedHero.getName());
            return;
        }

        System.out.println("Выберите героя:");
        System.out.println("1 - Герой 1");
        System.out.println("2 - Герой 2");
        System.out.println("3 - Герой 3");

        int heroChoice = scanner.nextInt();

        switch (heroChoice) {
            case 1:
                selectedHero = new Hero("Герой 1", 5, Team.HERO, 100, gameMap.getWidth(), gameMap.getHeight());
                break;
            case 2:
                selectedHero = new Hero("Герой 2", 4, Team.HERO, 80, gameMap.getWidth(), gameMap.getHeight());
                break;
            case 3:
                selectedHero = new Hero("Герой 3", 6, Team.HERO, 120, gameMap.getWidth(), gameMap.getHeight());
                break;
            default:
                System.out.println("Некорректный выбор. Пожалуйста, выберите номер героя из списка.");
                return;
        }

        System.out.println("Герой " + selectedHero.getName() + " был успешно выбран.");
    }

    public static Hero getSelectedHero() {
        return selectedHero;
    }

    public static Enemy getEnemy() {
        return enemy;
    }

    public static void enterCastle(GameMap gameMap, char castleType) {
        CastleManager castleManager = new CastleManager(gameMap, castleType);
        castleManager.isInCastle = true;
        System.out.println("Вы вошли в " + (castleType == 'C' ? "замок героя!" : "замок противника!"));
        castleManager.processCastleCommands();
    }


    public void exitCastle() {
        isInCastle = false;
        int[] heroCastlePos = gameMap.findHeroCastlePosition();
        int[] enemyCastlePos = gameMap.findEnemyCastlePosition();
        int castleX = -1, castleY = -1;
        int heroX = selectedHero.getHeroX();
        int heroY = selectedHero.getHeroY();

        // Проверяем, находится ли герой в замке
        if (mapManager.getMap()[heroY][heroX] == 'C') { // Замок героя
            mapManager.getMap()[heroY][heroX] = '.'; // Освобождаем клетку, где был герой

            // Если герой в замке героя, выходим из него
            if (heroCastlePos[0] != -1 && heroCastlePos[1] != -1) {
                castleX = heroCastlePos[0];
                castleY = heroCastlePos[1];
                int newY = castleY + 1; // Например, перемещение на клетку ниже

                if (newY < mapManager.getHeight() && mapManager.isWalkable(castleX, newY)) {
                    mapManager.getMap()[newY][castleX] = 'H'; // Размещаем героя в новой клетке
                    selectedHero.setHeroPosition(castleX, newY); // Обновляем позицию героя
                    System.out.println("Вы покинули замок героя и переместились на клетку ниже.");
                } else {
                    System.out.println("Вы не можете покинуть замок, нет свободной клетки ниже.");
                }

                mapManager.getMap()[castleY][castleX] = 'С'; // Убираем замок с карты
            }
        } else if (mapManager.getMap()[heroY][heroX] == 'E') { // Замок противника
            mapManager.getMap()[heroY][heroX] = '.'; // Освобождаем клетку, где был герой

            // Если герой в замке противника, выходим из него
            if (enemyCastlePos[0] != -1 && enemyCastlePos[1] != -1) {
                castleX = enemyCastlePos[0];
                castleY = enemyCastlePos[1];
                int newY = castleY + 1; // Например, перемещение на клетку ниже

                if (newY < mapManager.getHeight() && mapManager.isWalkable(castleX, newY)) {
                    mapManager.getMap()[newY][castleX] = 'H'; // Размещаем героя в новой клетке
                    selectedHero.setHeroPosition(castleX, newY); // Обновляем позицию героя
                    System.out.println("Вы покинули замок противника и переместились на клетку ниже.");
                } else {
                    System.out.println("Вы не можете покинуть замок, нет свободной клетки ниже.");
                }

                mapManager.getMap()[castleY][castleX] = 'E'; // Убираем замок противника с карты
            }
        } else {
            System.out.println("Вы не находитесь в замке.");
        }

        // Если это первый выход из замка
        if (isFirstExit) {
            gameMap.startGame(); // Запуск игры, если это первый выход
            isFirstExit = false; // После первого выхода флаг устанавливаем в false
        }

        // Обновляем флаг нахождения в замке


        gameMap.printMap(); // Печатаем карту один раз, после всех изменений
    }
}