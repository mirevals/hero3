package org.example.game.build;

import org.example.game.Player;
import org.example.game.battle.BattleField;
import org.example.game.map.MapManager;
import org.example.game.map.Position;
import org.example.game.person.*;
import org.example.game.map.GameMap;
import org.example.game.person.Character;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import static org.example.game.build.Shop.availableBuildings;

public class CastleManager {

    private static final Logger LOGGER = Logger.getLogger(CastleManager.class.getName());

    static {
        try {
            FileHandler fileHandler = new FileHandler("castle-access.log", true);
            fileHandler.setLevel(Level.SEVERE);
            fileHandler.setLevel(Level.WARNING);
            fileHandler.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(fileHandler);
            LOGGER.setUseParentHandlers(false); // отключаем консольный вывод
        } catch (IOException e) {
            throw new RuntimeException("Не удалось создать лог-файл", e);
        }
    }

    public static Object logCastlePosition(Object castleInstance) {
        try {
            Field positionField = castleInstance.getClass().getSuperclass().getDeclaredField("position");
            positionField.setAccessible(true);
            Object value = positionField.get(castleInstance);
            LOGGER.severe("Доступ к приватному полю 'position': " + value);
            return value;
        } catch (Exception e) {
            LOGGER.severe("Ошибка при доступе к полю 'position': " + e.getMessage());
            return null;
        }
    }

    public static boolean isInCastle;  // флаг, показывающий, находимся ли мы в замке
    private static boolean isFirstExit = true;
    public static boolean isFirstHero = true;
    public static boolean isNoGuardPost = true;
    public static boolean isNoUnitsBuy = true;
    public static boolean isTavernNotBuild = true;

    private static Scanner scanner = new Scanner(System.in);  // Один Scanner на всю программу


    public static void enterCastle(Castle castle, Hero hero, Player player, Enemy enemy,
                                     EnemyCastle enemyCastle, HeroCastle heroCastle,
                                     GameMap gameMap, MapManager mapManager,
                                     List<Unit> buyUnit, Character character,
                                     BattleField battleField, List<Unit> allUnits, Carriage carriage) {
        isInCastle = true;
        String castleName = (castle.getType() == Castle.CastleType.HERO) ? "замок героя!" : "замок противника!";
        System.out.println("Вы вошли в " + castleName);

        // Проверка на null
        if (castle == null) {
            System.out.println("Ошибка: замок не был инициализирован.");
            return;
        }

        processCastleCommands(castle, hero, enemy, player, enemyCastle, heroCastle, gameMap, mapManager, buyUnit, character, battleField, allUnits, carriage);
    }

    public static void processCastleCommands(Castle castle, Hero hero, Enemy enemy, Player player,
                                             EnemyCastle enemyCastle, HeroCastle heroCastle,
                                             GameMap gameMap, MapManager mapManager,
                                             List<Unit> buyUnit, Character character,
                                             BattleField battleField, List<Unit> allUnits, Carriage carriage) {
        while (isInCastle) {
            if (isTavernNotBuild) {
                handleTavernNotBuilt(castle);
            } else if (isFirstHero) {
                handleFirstHero(castle, hero, player, buyUnit);
            } else if (isNoGuardPost) {
                handleGuardNotBuilt(castle);
            } else if (isNoUnitsBuy) {
                handleUnitsBuy(castle, hero, player, buyUnit);
            } else {
                showCastleCommands();
                String command = scanner.nextLine().trim().toLowerCase();
                processCastleAction(command, castle, enemy, hero, player, enemyCastle, heroCastle, gameMap, mapManager, buyUnit, character, battleField, allUnits, carriage);
            }
        }
    }

    private static void handleUnitsBuy(Castle castle, Hero hero, Player player, List<Unit> buyUnit) {
        System.out.println("Вы должны купить юнитов для героя в Посте перед тем, как выйти из замка.");
        System.out.println("Введите 'b' для входа в список построек.");

        String command = scanner.nextLine().trim().toLowerCase();

        if (command.equals("b")) {
            openStorage(scanner, castle, hero, player, buyUnit);
        } else {
            System.out.println("Пожалуйста, выберите юнитов для героя перед тем, как продолжить.");
        }
    }

    private static void handleGuardNotBuilt(Castle castle) {
        System.out.println("Вы должны купить Пост перед тем, как выйти из замка.");
        System.out.println("Введите 'm' для входа в магазин.");
        String command = scanner.nextLine().trim().toLowerCase();

        if (command.equals("m")) {
            openShop(scanner, castle);
        } else {
            System.out.println("Пожалуйста, купите Пост перед тем, как продолжить.");
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

    private static void handleFirstHero(Castle castle, Hero hero, Player player, List<Unit> buyUnit) {
        System.out.println("Вы должны купить героя в Таверне перед тем, как выйти из замка.");
        System.out.println("Введите 'b' для входа в список построек.");
        String command = scanner.nextLine().trim().toLowerCase();

        if (command.equals("b")) {
            openStorage(scanner, castle, hero, player, buyUnit);  // Покупка героя
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

    private static void processCastleAction(String command, Castle castle, Enemy enemy, Hero hero,
                                            Player player, EnemyCastle enemyCastle,
                                            HeroCastle heroCastle, GameMap gameMap,
                                            MapManager mapManager, List<Unit> buyUnit,
                                            Character character, BattleField battleField,
                                            List<Unit> allUnits, Carriage carriage) {
        switch (command) {
            case "q":
                exitCastle(enemy, enemyCastle, heroCastle, hero, player, gameMap, castle, mapManager, buyUnit, character, battleField, allUnits, carriage);  // Выход из замка
                break;
            case "m":
                openShop(scanner, castle);
                break;
            case "b":
                openStorage(scanner, castle, hero, player, buyUnit);
                break;
            default:
                System.out.println("Неизвестная команда. Введите 'h' для справки.");
        }
    }

    private static void openShop(Scanner scanner, Castle castle) {
        System.out.println("Добро пожаловать в магазин!");
        Shop.showAvailableBuildings();

        while (true) {
            System.out.println("Введите номер здания, которое вы хотите купить (или 0 для выхода):");
            try {
                int buildingChoice = scanner.nextInt();
                scanner.nextLine(); // очистка буфера

                if (buildingChoice == 0) {
                    System.out.println("Выход из магазина.");
                    break;
                }

                Building purchasedBuilding = buyBuilding(buildingChoice, castle);
                if (purchasedBuilding != null) {
                    System.out.println("Здание " + purchasedBuilding.getName() + " добавлено в ваш замок.");
                    break; // покупка завершена — выходим
                } else {
                    System.out.println("Невозможно купить здание. Попробуйте снова.");
                }

            } catch (InputMismatchException e) {
                scanner.nextLine(); // очистка некорректного ввода
                LOGGER.log(Level.WARNING, "Ошибка ввода: ожидалось число для выбора здания.", e);
                System.out.println("Некорректный ввод. Пожалуйста, введите номер здания.");
            }
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
                isTavernNotBuild = false;
                System.out.println("Таверна была построена.");

            }

            if (building instanceof GuardPost) {
                isNoGuardPost = false;
                System.out.println("Пост был построен.");
            }

            return building;
        } else {
            // Если выбор неверен, выводим сообщение
            System.out.println("Неверный выбор здания.");
            return null; // Возвращаем null, если выбор был неверным
        }
    }

    private static void openStorage(Scanner scanner, Castle castle, Hero hero, Player player, List<Unit> buyUnit) {
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
            if (buildingChoice == 1) {
                openTavern(scanner, hero, player );
            } else if (buildingChoice == 2) {
                openGuardPost(buyUnit, hero, player);
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

    private static void openGuardPost(List<Unit> buyUnit, Hero hero, Player player) {

        System.out.println("Добро пожаловать в Пост!");

        GuardPost.displayAvailableUnits(buyUnit);

        System.out.println("Введите номер юнита, которого хотите купить:");
        int heroChoice = scanner.nextInt();
        scanner.nextLine();

        if (GuardPost.buyUnit(heroChoice, hero, player, buyUnit)) {
            if (!hero.getUnits().isEmpty()) {
                System.out.println("Юниты " + hero.getName() + " был успешно добавлены.");
                isNoUnitsBuy = false;
            }
        } else {
            System.out.println("Не удалось купить героя.");
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

    public static void exitCastle(Enemy enemy, EnemyCastle enemyCastle, HeroCastle heroCastle,
                                  Hero hero, Player player, GameMap gameMap, Castle castle,
                                  MapManager mapManager, List<Unit> buyUnit, Character character,
                                  BattleField battleField, List<Unit> allUnits, Carriage carriage) {
        if (!isInCastle) {
            System.out.println("Вы не находитесь в замке.");
            return;
        }
        Character.CharacterType characterType = character.getType();

        Position characterPos = character.getPosition();
        Position heroCastlePos = heroCastle.getPosition();
        Position enemyCastlePos = enemyCastle.getPosition();

        // Определяем направление выхода в зависимости от типа замка
        int direction;

        Castle currentCastle = null;

        // Определяем, в каком замке находится персонаж
        if (characterType == Character.CharacterType.HERO) {
            // Если герой находится в замке героя
            if (characterPos.equals(heroCastlePos)) {
                currentCastle = heroCastle;
            }
            // Если герой находится в замке врага
            else if (characterPos.equals(enemyCastlePos)) {
                currentCastle = enemyCastle;
            } else {
                System.out.println("Ошибка: герой не находится в замке.");
                return;
            }
        }
        else if (characterType == Character.CharacterType.ENEMY) {
            // Если враг находится в замке противника
            if (characterPos.equals(enemyCastlePos)) {
                currentCastle = enemyCastle;
            }
            // Если враг находится в замке героя
            else if (characterPos.equals(heroCastlePos)) {
                currentCastle = heroCastle;
            } else {
                System.out.println("Ошибка: враг не находится в замке.");
                return;
            }
        }

        // Определяем направление выхода
        if (currentCastle == heroCastle) {
            direction = 1;
        } else if (currentCastle == enemyCastle) {
            direction = -1; // Выход вверх из замка противника
        } else {
            System.out.println("Ошибка: герой не находится в замке.");
            return;
        }

        int newX = characterPos.getX() + direction;
        int newY = characterPos.getY();
        char heroSymbol = character.getType() == Character.CharacterType.HERO ? 'H' : 'A';
        char[][] map = gameMap.getMap();

        if (mapManager.isWalkable(newX, newY, gameMap)) {
            map[newY][newX] = heroSymbol;
            character.setPosition(newX, newY);
            gameMap.setCellValue(newX, newY, heroSymbol);
            System.out.println("Вы покинули " + (currentCastle.getType() == Castle.CastleType.HERO ? "замок героя" : "замок противника") + " и переместились " + (direction == 1 ? "вправо." : "влево."));
        } else {
            System.out.println("Вы не можете покинуть замок, нет свободной клетки " + (direction == 1 ? "внизу." : "вверху."));
            return;
        }

        // Восстанавливаем символ замка
        map[heroCastle.getPosition().getY()][heroCastle.getPosition().getX()] = 'C';
        map[enemyCastle.getPosition().getY()][enemyCastle.getPosition().getX()] = 'E';

        // Устанавливаем флаг выхода из замка
        isInCastle = false;

        // Если это первый выход, запускаем игру
        if (isFirstExit) {
            mapManager.startGame(hero, enemy, heroCastle, player, enemyCastle, heroCastle, gameMap, mapManager, buyUnit, battleField, allUnits, carriage);
            isFirstExit = false;
        }
    }
}