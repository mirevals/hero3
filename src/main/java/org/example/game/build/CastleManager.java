package org.example.game.build;

import org.example.game.map.MapManager;
import org.example.game.map.Position;
import org.example.game.person.Character;
import org.example.game.map.GameMap;
import org.example.game.person.Enemy;
import org.example.game.person.Hero;
import org.example.game.person.Team;

import java.util.Scanner;
public class CastleManager {
    private final GameMap gameMap;
    public static boolean isInCastle;  // флаг, показывающий, находимся ли мы в замке
    private final CastleType castleType;
    private final Shop shop;
    private Hero hero = null;
    private static boolean isFirstExit = true;
    static Enemy enemy;
    private final HeroCastle heroCastle;
    private final EnemyCastle enemyCastle;

    private static Scanner scanner = new Scanner(System.in);  // Один Scanner на всю программу

    MapManager mapManager;


    public enum CastleType {
        HERO, ENEMY
    }

    public CastleManager(GameMap gameMap, CastleType castleType) {
        this.gameMap = gameMap;
        enemy = new Enemy("Враг", 5, Team.ENEMY, 100, gameMap.getWidth(), gameMap.getHeight());
        this.castleType = castleType;
        this.shop = new Shop();
        this.heroCastle = new HeroCastle(gameMap.getHeight(), gameMap.getWidth());
        this.enemyCastle = new EnemyCastle(gameMap.getHeight(), gameMap.getWidth());


    }

    public void processCastleCommands(CastleType castleType) {
        while (isInCastle) {
            if (hero == null) {
                System.out.println("Вы должны выбрать героя перед тем, как выйти из замка.");
                System.out.println("Введите 'h' для выбора героя.");
                String command = scanner.nextLine().trim().toLowerCase();

                if (command.equals("h")) {
                    chooseHero(scanner);
                } else {
                    System.out.println("Пожалуйста, выберите героя перед тем, как продолжить.");
                }
            } else {

                mapManager = new MapManager(gameMap.getWidth(), gameMap.getHeight(), this.hero, this.enemy);

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
                        exitCastle(hero, castleType);
                        break;
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
        if (hero != null) {
            System.out.println("Герой уже выбран: " + hero.getName());
            return;
        }

        System.out.println("Выберите героя:");
        System.out.println("1 - Герой 1");
        System.out.println("2 - Герой 2");
        System.out.println("3 - Герой 3");

        int heroChoice = scanner.nextInt();
        scanner.nextLine();  // Очищаем оставшийся `\n`

        switch (heroChoice) {
            case 1:
                hero = new Hero("Герой 1", 15, Team.HERO, 1000, gameMap.getWidth(), gameMap.getHeight());
                break;
            case 2:
                hero = new Hero("Герой 2", 14, Team.HERO, 800, gameMap.getWidth(), gameMap.getHeight());
                break;
            case 3:
                hero = new Hero("Герой 3", 16, Team.HERO, 1200, gameMap.getWidth(), gameMap.getHeight());
                break;
            default:
                System.out.println("Некорректный выбор. Пожалуйста, выберите номер героя из списка.");
                return;
        }

        System.out.println("Герой " + hero.getName() + " был успешно выбран.");
    }


    public static Enemy getEnemy() {
        return enemy;
    }

    public static void enterCastle(int width, int height, CastleType castleType) {

        GameMap gameMap = new GameMap(width, height);
        CastleManager castleManager = new CastleManager(gameMap, castleType);
        castleManager.isInCastle = true;

        String castleName = (castleType == CastleType.HERO) ? "замок героя!" : "замок противника!";
        System.out.println("Вы вошли в " + castleName);

        castleManager.processCastleCommands(castleType);
    }

    public void exitCastle(Character character, CastleType castleType) {
        Position castlePos = (castleType == CastleType.HERO) ? heroCastle.getPosition() : enemyCastle.getPosition();
        char castleSymbol = (castleType == CastleType.HERO) ? 'C' : 'E';
        char characterSymbol = (castleType == CastleType.HERO) ? 'H' : 'A';

        int heroX = hero.getHeroX();
        int heroY = hero.getHeroY();
        char[][] map = mapManager.getMap();

        // Проверяем, действительно ли герой находится в замке
        if (isInCastle) {
            if (isFirstExit) {
                // Определяем новую позицию для выхода (всегда вниз)
                int newX = castlePos.getX();
                int newY = castlePos.getY() + 1;

                if (mapManager.isWalkable(newX, newY)) {
                    map[newY][newX] = characterSymbol;
                    hero.setHeroPosition(newX, newY);
                    System.out.println("Вы покинули " + (castleType == CastleType.HERO ? "замок героя" : "замок противника") + " и переместились на клетку ниже.");
                } else {
                    System.out.println("Вы не можете покинуть замок, нет свободной клетки ниже.");
                }
                map[castlePos.getY()][castlePos.getX()] = castleSymbol;


                mapManager.startGame(hero, getEnemy());
                isFirstExit = false;
            } else {
                System.out.println("Вы не находитесь в замке.");
            }
            return;
        }

        // Определяем новую позицию для выхода (всегда вниз)
        int newX = castlePos.getX();
        int newY = castlePos.getY() + 2;

        if (mapManager.isWalkable(newX, newY)) {
            map[newY][newX] = characterSymbol;
            hero.setHeroPosition(newX, newY);
            System.out.println("Вы покинули " + (castleType == CastleType.HERO ? "замок героя" : "замок противника") + " и переместились на клетку ниже.");
        } else {
            System.out.println("Вы не можете покинуть замок, нет свободной клетки ниже.");
        }

        // Восстанавливаем символ замка
        map[castlePos.getY()][castlePos.getX()] = castleSymbol;
        isInCastle = false;

        mapManager.printMap(); // Печатаем карту после всех изменений

    }
}