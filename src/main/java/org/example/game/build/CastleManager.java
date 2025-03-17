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
    private static final GameMap gameMap;
    public static boolean isInCastle;  // флаг, показывающий, находимся ли мы в замке
    private final Shop shop;
    private final Storage storage;
    private static boolean isFirstExit = true;
    private static boolean isFirstHero = true;
    static boolean isTavernNotBuild = true;

    static Hero hero;
    static Character character = hero;
    static Enemy enemy;

    private static HeroCastle heroCastle;
    private static EnemyCastle enemyCastle;

    private static Scanner scanner;  // Один Scanner на всю программу
    static final Player player;

    private static CastleManager castleManager;

    static {
        gameMap = new GameMap(10, 10);
        heroCastle = new HeroCastle(10, 10);
        enemyCastle = new EnemyCastle(10, 10);
        enemy = new Enemy("Враг", 5, Team.ENEMY, 100, gameMap.getWidth(), gameMap.getHeight());
        player  = new Player(1000);
        scanner  = new Scanner(System.in);
    }

    public static CastleManager getInstance(GameMap gameMap, Castle castle, Player player) {
        if (castleManager == null) {
            castleManager = new CastleManager(gameMap, castle, player);
        }
        return castleManager;
    }

    public CastleManager(GameMap gameMap, Castle castle, Player player) {

        this.shop = new Shop();
        this.storage = new Storage();
    }

    public Hero getHero() {
        return hero;
    }

    public void processCastleCommands(Castle castle, Hero hero, Enemy enemy, Character character) {
        while (isInCastle) {
            if (isTavernNotBuild) {
                handleTavernNotBuilt(castle);
            } else if (isFirstHero) {
                handleFirstHero();
            } else {
                showCastleCommands();
                String command = scanner.nextLine().trim().toLowerCase();
                processCastleAction(command, castle, hero, enemy, character);
            }
        }
    }

    private void handleTavernNotBuilt(Castle castle) {
        System.out.println("Вы должны купить таверну перед тем, как выйти из замка.");
        System.out.println("Введите 'm' для входа в магазин.");
        String command = scanner.nextLine().trim().toLowerCase();

        if (command.equals("m")) {
            openShop(scanner, castle);  // Открываем магазин для покупки Таверны
        } else {
            System.out.println("Пожалуйста, купите Таверну перед тем, как продолжить.");
        }
    }

    private void handleFirstHero() {
        System.out.println("Вы должны купить героя в Таверне перед тем, как выйти из замка.");
        System.out.println("Введите 'b' для входа в список построек.");
        String command = scanner.nextLine().trim().toLowerCase();

        if (command.equals("b")) {
            openStorage(scanner, heroCastle);  // Покупка героя
        } else {
            System.out.println("Пожалуйста, выберите героя перед тем, как продолжить.");
        }
    }

    private void showCastleCommands() {
        System.out.println("Вы в замке. Доступные команды:");
        System.out.println("q - выйти из замка");
        System.out.println("hh - помощь (список команд)");
        System.out.println("v - взаимодействовать с NPC");
        System.out.println("h - выбрать героя");
        System.out.println("m - открыть магазин");
        System.out.println("b - показать список построек");
    }

    private void processCastleAction(String command, Castle castle, Hero hero, Enemy enemy, Character character) {
        switch (command) {
            case "q":
                exitCastle(character, hero, enemy, castle);  // Выход из замка
                break;
            case "m":
                openShop(scanner, castle);
                break;
            case "b":
                openStorage(scanner, castle);
                break;
            default:
                System.out.println("Неизвестная команда. Введите 'h' для справки.");
        }
    }

    private void openShop(Scanner scanner, Castle castle) {
        System.out.println("Добро пожаловать в магазин!");
        shop.showAvailableBuildings();

        System.out.println("Введите номер здания, которое вы хотите купить:");
        int buildingChoice = scanner.nextInt();
        scanner.nextLine();

        Building purchasedBuilding = buyBuilding(buildingChoice, castle);

        if (purchasedBuilding != null) {
            System.out.println("Здание " + purchasedBuilding.getName() + " добавлено в ваш замок.");
        }
    }

    public Building buyBuilding(int buildingChoice, Castle castle) {
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

    private void openStorage(Scanner scanner, Castle castle) {
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
                openTavern(scanner);
            } else {
                Storage selectBuilding = storage.useBuilding(buildingChoice, castle);
                if (selectBuilding != null) {
                    System.out.println("Вы успешно использовали здание.");
                } else {
                    System.out.println("Ошибка: не удалось использовать здание.");
                }
            }
        }
    }

    private void openTavern(Scanner scanner) {
        System.out.println("Добро пожаловать в таверну!");

        Tavern.showTavernInfo();

        System.out.println("Введите номер героя, которого хотите купить:");
        int heroChoice = scanner.nextInt();
        scanner.nextLine();

        if (Tavern.buyHero(heroChoice, player)) {
            CastleManager.hero = Tavern.createHero(heroChoice, gameMap.getWidth(), gameMap.getHeight());
            if (hero != null) {
                System.out.println("Герой " + hero.getName() + " был успешно создан.");
                CastleManager.character = hero;
                isFirstHero = false;
            }
        } else {
            System.out.println("Не удалось купить героя.");
        }
    }



    public static void enterCastle(Castle.CastleType castleType) {
        isInCastle = true;
        String castleName = (castleType == Castle.CastleType.HERO) ? "замок героя!" : "замок противника!";
        System.out.println("Вы вошли в " + castleName);

        Castle castle = (castleType == Castle.CastleType.HERO) ? heroCastle : enemyCastle;  // Используем правильный замок

        // Проверка на null
        if (castle == null) {
            System.out.println("Ошибка: замок не был инициализирован.");
            return;
        }

        // Используем синглтон для получения экземпляра CastleManager
        CastleManager manager = CastleManager.getInstance(gameMap, castle, player);
        manager.processCastleCommands(castle, hero, enemy, character);
    }

    public void exitCastle(Character character, Hero hero, Enemy enemy, Castle castle) {
        Position castlePos = (castle.getType() == Castle.CastleType.HERO) ? heroCastle.getPosition() : enemyCastle.getPosition();
        char castleSymbol = (castle.getType() == Castle.CastleType.HERO) ? 'C' : 'E';
        char characterSymbol = (castle.getType() == Castle.CastleType.HERO) ? 'H' : 'A';

        int heroX = character.getX();
        int heroY = character.getY();
        MapManager mapManager = new MapManager(gameMap.getWidth(), gameMap.getHeight(), heroCastle, enemyCastle, hero, enemy);
        char[][] map = mapManager.getMap();

        // Проверяем, действительно ли герой находится в замке
        if (isInCastle) {
            if (isFirstExit) {
                // Определяем новую позицию для выхода (всегда вниз)
                int newX = castlePos.getX();
                int newY = castlePos.getY() + 1;

                if (mapManager.isWalkable(newX, newY)) {
                    map[newY][newX] = characterSymbol;
                    character.setPosition(newX, newY);
                    System.out.println("Вы покинули " + (castle.getType() == Castle.CastleType.HERO ? "замок героя" : "замок противника") + " и переместились на клетку ниже.");
                } else {
                    System.out.println("Вы не можете покинуть замок, нет свободной клетки ниже.");
                }
                map[castlePos.getY()][castlePos.getX()] = castleSymbol;


                mapManager.startGame(character,hero, enemy, castle.getType());
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
            character.setPosition(newX, newY);
            System.out.println("Вы покинули " + (castle.getType() == Castle.CastleType.HERO ? "замок героя" : "замок противника") + " и переместились на клетку ниже.");
        } else {
            System.out.println("Вы не можете покинуть замок, нет свободной клетки ниже.");
        }

        // Восстанавливаем символ замка
        map[castlePos.getY()][castlePos.getX()] = castleSymbol;
        isInCastle = false;

        mapManager.printMap(); // Печатаем карту после всех изменений

    }

}