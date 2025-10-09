package org.example;

import org.example.account.AccountManager;
import org.example.account.Account;
import org.example.game.Game;
import org.example.game.Player;
import org.example.game.Virus;
import org.example.game.build.EnemyCastle;
import org.example.game.build.HeroCastle;
import org.example.game.map.GameMap;
import org.example.game.map.MapManager;
import org.example.game.map.Position;
import org.example.game.person.Carriage;
import org.example.game.person.Enemy;
import org.example.game.person.Hero;
import org.example.game.person.Unit;
import org.example.game.map.Road;
import org.example.game.battle.BattleField;
import org.example.game.person.Team;
import org.example.game.save.GameState;
import org.example.game.save.SaveManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        // This main method can be used for initial setup or simple testing.
        // The actual game loop will be started via App.java or directly by calling startGame().
        System.out.println("Starting game via Main.main()... (This is for testing purposes)");

        // Example setup for direct startGame call
        AccountManager accountManager = new AccountManager();
        accountManager.register("TestAccount", "password");
        accountManager.login("TestAccount", "password");
        Account playerAccount = accountManager.getCurrentAccount().orElseThrow(() -> new IllegalStateException("No active account found"));

        GameMap gameMap = new GameMap(10, 10);
        HeroCastle heroCastle = new HeroCastle(gameMap.getHeight(), gameMap.getWidth());
        EnemyCastle enemyCastle = new EnemyCastle(gameMap.getHeight(), gameMap.getWidth());
        Hero hero = new Hero("Hero", 10, Team.HERO, 100, gameMap.getWidth(), gameMap.getHeight(), 100, 10, 5, 3, new ArrayList<Unit>());
        Enemy enemy = new Enemy("Enemy", 5, Team.ENEMY, 50, gameMap.getWidth(), gameMap.getHeight(), 50, 8, 3, 2, new ArrayList<Unit>());
        Road road = new Road(0, gameMap.getHeight() / 2, gameMap.getWidth() - 1, gameMap.getHeight() / 2);
        Carriage carriage = new Carriage(new Position(gameMap.getWidth() / 2, gameMap.getHeight() / 2), 1, 5, Carriage.Direction.RIGHT);
        List<Unit> allUnits = new ArrayList<>();
        allUnits.addAll(hero.getUnits());
        allUnits.addAll(enemy.getUnits());

        Player player = new Player(hero.getName(), hero.getGold());

        GameState initialGameState = new GameState(
            playerAccount.getUsername(),
            player,
            gameMap,
            hero,
            enemy,
            heroCastle,
            enemyCastle,
            allUnits,
            carriage,
            road,
            playerAccount.isInfected(),
            playerAccount.getViruses()
        );

        Scanner scanner = new Scanner(System.in);
        startGame(initialGameState, scanner);
        scanner.close();
    }

    public static void startGame(GameState gameState, Scanner scanner) {
        // Используем существующий AccountManager вместо создания нового
        AccountManager accountManager = App.getAccountManager();
        if (accountManager == null) {
            throw new IllegalStateException("AccountManager not initialized");
        }
        
        // Получаем текущий аккаунт
        Account playerAccount = accountManager.getCurrentAccount()
            .orElseThrow(() -> new IllegalStateException("No active account found"));
        
        // Проверяем, что имя игрока совпадает с текущим аккаунтом
        if (!playerAccount.getUsername().equals(gameState.getPlayerName())) {
            throw new IllegalStateException("GameState player name does not match current account");
        }
        
        // Восстанавливаем состояние заражения из GameState
        if (gameState.isAccountInfected()) {
            playerAccount.setInfected(true);
            for (Virus virus : gameState.getAccountViruses()) {
                playerAccount.addVirus(virus);
            }
        }

        GameMap gameMap = gameState.getGameMap();
        HeroCastle heroCastle = gameState.getHeroCastle();
        EnemyCastle enemyCastle = gameState.getEnemyCastle();
        Hero hero = gameState.getHero();
        Enemy enemy = gameState.getEnemy();
        Road road = gameState.getRoad();
        Carriage carriage = gameState.getCarriage();
        List<Unit> allUnits = gameState.getAllUnits();
        Player player = gameState.getPlayer();

        BattleField battleField = new BattleField(allUnits);
        Game game = new Game(accountManager, gameMap);
        MapManager mapManager = new MapManager(
                heroCastle,
                enemyCastle,
                enemy,
                hero,
                gameMap,
                road,
                carriage,
                gameState.isAccountInfected(),
                gameState.getAccountViruses()
        );

        game.setMapManager(mapManager);
        mapManager.setGame(game);

        // Handle map transition and show infection message if account is infected
        game.handleMapTransition("current_map");

        SaveManager saveManager = new SaveManager();

        while (true) {
            System.out.println("\n--- Turn " + (game.getTurnCount() + 1) + " ---");

            System.out.println("Ход героя:");
            System.out.println("w - вверх");
            System.out.println("s - вниз");
            System.out.println("a - влево");
            System.out.println("d - вправо");
            System.out.println("q - выход");
            System.out.println("b - купить юнитов/здания");
            System.out.println("m - показать карту");

            System.out.print("Введите команду: ");
            String command = scanner.nextLine();

            if (command.equals("q")) {
                System.out.println("Хотите сохранить игру перед выходом? (y/n)");
                String saveChoice = scanner.nextLine().trim().toLowerCase();
                if (saveChoice.equals("y")) {
                    // Create new GameState with current infection state
                    GameState newGameState = new GameState(
                        playerAccount.getUsername(),
                        player,
                        gameMap,
                        hero,
                        enemy,
                        heroCastle,
                        enemyCastle,
                        allUnits,
                        carriage,
                        road,
                        playerAccount.isInfected(),
                        playerAccount.getViruses()
                    );
                    saveManager.saveGame(playerAccount.getUsername(), newGameState, false);
                }
                System.out.println("Возвращение в главное меню...");
                break;
            }

            int dx = 0, dy = 0;
            int steps = 1;
            boolean moved = false;

            switch (command) {
                case "w": dy = -1; moved = true; break;
                case "s": dy = 1; moved = true; break;
                case "a": dx = -1; moved = true; break;
                case "d": dx = 1; moved = true; break;
                case "b":
                    System.out.println("Открыт магазин! (Placeholder)");
                    break;
                case "m":
                    gameMap.displayMap();
                    break;
                default:
                    System.out.println("Неверная команда. Попробуйте снова.");
                    break;
            }

            if (moved) {
                mapManager.moveHero(dx, dy, steps, hero, enemy, heroCastle, player, enemyCastle, heroCastle, gameMap, mapManager, new ArrayList<>(), hero, battleField, allUnits, carriage);
            }

            // Check if hero needs to buy more steps after moving
            if (!hero.canMove()) {
                mapManager.offerToBuySteps(hero); // Pass the hero character to the method
            }

            game.nextTurn();

            mapManager.enemyMove(hero, enemy, heroCastle, player, enemyCastle, heroCastle, gameMap, mapManager, new ArrayList<>(), battleField, allUnits, carriage);
            mapManager.moveCarriage(carriage, gameMap, hero);

            gameMap.displayMap();
        }

        System.out.println("\n--- Game Simulation End ---");
        System.out.println("Final viruses on map: " + game.getActiveVirusesOnMap().size());
        System.out.println("Final viruses attached to account: " + playerAccount.getViruses().size());
        // scanner.close(); // Do not close scanner here, it will be closed in App.java
    }
} 