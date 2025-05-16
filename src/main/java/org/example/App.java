package org.example;

import org.example.game.Player;
import org.example.game.battle.Battle;
import org.example.game.battle.BattleField;
import org.example.game.build.*;
import org.example.game.map.*;
import org.example.game.person.*;
import org.example.game.save.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.example.game.build.Shop.availableBuildings;

public class App {
    private static SaveManager saveManager;
    private static String playerName;
    private static Scanner scanner;

    public static void main(String[] args) {
        scanner = new Scanner(System.in);
        saveManager = new SaveManager();
        
        System.out.println("Добро пожаловать в игру!");
        System.out.print("Введите ваше имя: ");
        playerName = scanner.nextLine();
        
        while (true) {
            System.out.println("\nГлавное меню:");
            System.out.println("1. Новая игра");
            System.out.println("2. Загрузить игру");
            System.out.println("3. Редактор карт");
            System.out.println("4. Выход");
            
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline
            
            switch (choice) {
                case 1:
                    startNewGame();
                    break;
                case 2:
                    loadGame();
                    break;
                case 3:
                    MapEditor editor = new MapEditor();
                    editor.start();
                    break;
                case 4:
                    System.out.println("До свидания!");
                    System.exit(0);
                default:
                    System.out.println("Неверный выбор!");
            }
        }
    }

    private static void startNewGame() {
        GameMap gameMap = selectMap();
        if (gameMap == null) {
            return;
        }

        // Создаем игрока
        Player player = new Player(playerName, 1000);

        // Создаем остальные объекты игры
        GameState gameState = initializeGameState(player, gameMap);
        startGameLoop(gameState);
    }

    private static void loadGame() {
        List<String> saves = saveManager.getAvailableSaves(playerName);
        if (saves.isEmpty()) {
            System.out.println("Нет доступных сохранений!");
            return;
        }

        System.out.println("\nДоступные сохранения:");
        for (int i = 0; i < saves.size(); i++) {
            System.out.println((i + 1) + ". " + saves.get(i));
        }

        System.out.println("Выберите сохранение (номер):");
        int choice = scanner.nextInt();
        scanner.nextLine(); // consume newline

        if (choice < 1 || choice > saves.size()) {
            System.out.println("Неверный выбор!");
            return;
        }

        String saveName = saves.get(choice - 1);
        GameState gameState = saveManager.loadGame(playerName, saveName);
        if (gameState != null) {
            startGameLoop(gameState);
        }
    }

    private static GameMap selectMap() {
        List<String> maps = MapEditor.getAvailableMaps();
        if (maps.isEmpty()) {
            System.out.println("Нет доступных карт! Создайте карту в редакторе.");
            return null;
        }

        System.out.println("\nВыберите карту для игры:");
        for (int i = 0; i < maps.size(); i++) {
            System.out.println((i + 1) + ". " + maps.get(i));
        }

        int mapChoice = scanner.nextInt();
        scanner.nextLine(); // consume newline

        if (mapChoice < 1 || mapChoice > maps.size()) {
            System.out.println("Неверный выбор карты!");
            return null;
        }

        try {
            String mapPath = "maps/" + maps.get(mapChoice - 1);
            GameMap gameMap = MapManager.loadMap(mapPath);
            if (gameMap == null) {
                System.out.println("Ошибка загрузки карты!");
                return null;
            }
            return gameMap;
        } catch (Exception e) {
            System.out.println("Ошибка при загрузке карты: " + e.getMessage());
            return null;
        }
    }

    private static GameState initializeGameState(Player player, GameMap gameMap) {
        Unit warrior = new Unit(Unit.UnitType.WARRIOR, 100, 100, 1, 10, Team.HERO, 'W', 100);
        List<Unit> unitsHero = new ArrayList<>();
        unitsHero.add(warrior);

        Unit enemyUnit = new Unit(Unit.UnitType.WARRIOR, 100, 100, 1, 10, Team.ENEMY, 'A', 100);
        List<Unit> unitsEnemy = new ArrayList<>();
        unitsEnemy.add(enemyUnit);

        List<Unit> allUnits = new ArrayList<>();
        allUnits.addAll(unitsHero);
        allUnits.addAll(unitsEnemy);

        Hero hero = new Hero("Герой 1", 10, Team.HERO, 1000, gameMap.getWidth(), gameMap.getHeight(), 100, 100, 100, 3, unitsHero);
        Enemy enemy = new Enemy("Враг", 5, Team.ENEMY, 100, gameMap.getWidth(), gameMap.getHeight(), 100, 100, 100, 1, unitsEnemy);

        HeroCastle heroCastle = new HeroCastle(gameMap.getHeight(), gameMap.getWidth());
        EnemyCastle enemyCastle = new EnemyCastle(gameMap.getHeight(), gameMap.getWidth());

        Building building1 = availableBuildings.get(0);
        Building building2 = availableBuildings.get(1);
        enemyCastle.addBuilding(building1);
        enemyCastle.addBuilding(building2);

        Carriage carriage = new Carriage(new Position(5, 0), 1, 10, Carriage.Direction.DOWN);
        Road road = new Road(gameMap.getWidth() / 6, gameMap.getHeight() / 4, 5 * gameMap.getWidth() / 6, gameMap.getHeight() / 4);

        return new GameState(playerName, player, gameMap, hero, enemy, heroCastle, enemyCastle, allUnits, carriage, road);
    }

    private static void startGameLoop(GameState gameState) {
        BattleField battleField = new BattleField(gameState.getAllUnits());
        MapManager mapManager = new MapManager(gameState.getHeroCastle(), gameState.getEnemyCastle(), 
            gameState.getEnemy(), gameState.getHero(), gameState.getGameMap(), gameState.getRoad(), gameState.getCarriage());

        // Запускаем игровой цикл
        while (true) {
            System.out.println("\nИгровое меню:");
            System.out.println("1. Продолжить игру");
            System.out.println("2. Сохранить игру");
            System.out.println("3. Выйти в главное меню");
            
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline
            
            switch (choice) {
                case 1:
                    mapManager.startGame(gameState.getHero(), gameState.getEnemy(), gameState.getHeroCastle(),
                        gameState.getPlayer(), gameState.getEnemyCastle(), gameState.getHeroCastle(),
                        gameState.getGameMap(), mapManager, new ArrayList<>(), battleField,
                        gameState.getAllUnits(), gameState.getCarriage());
                    break;
                case 2:
                    saveManager.saveGame(playerName, gameState, false);
                    break;
                case 3:
                    return;
                default:
                    System.out.println("Неверный выбор!");
            }
        }
    }
}

//Battle.autoFight(battleField, allUnits);