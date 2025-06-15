package org.example;

import org.example.game.Player;
import org.example.game.battle.Battle;
import org.example.game.battle.BattleField;
import org.example.game.build.*;
import org.example.game.map.*;
import org.example.game.person.*;
import org.example.game.save.*;
import org.example.game.score.HighScoreManager;
import org.example.account.AccountManager;
import org.example.account.Account;
import org.example.game.Game;
import org.example.game.Virus;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Optional;

import static org.example.game.build.Shop.availableBuildings;
import static org.example.game.person.Unit.UnitType.WARRIOR;

public class App {
    private static SaveManager saveManager;
    private static HighScoreManager highScoreManager;
    private static AccountManager accountManager;
    private static Account currentAccount;
    private static Scanner scanner;

    public static AccountManager getAccountManager() {
        return accountManager;
    }

    public static void main(String[] args) {
        scanner = new Scanner(System.in);
        saveManager = new SaveManager();
        highScoreManager = new HighScoreManager();
        accountManager = new AccountManager();
        
        System.out.println("Добро пожаловать в игру!");
        
        // Сначала обрабатываем вход в аккаунт
        handleAccountLogin();
        
        while (true) {
            System.out.println("\nГлавное меню:");
            System.out.println("1. Рекорды");
            System.out.println("2. Новая игра");
            System.out.println("3. Загрузить игру");
            System.out.println("4. Редактор карт");
            System.out.println("5. Сменить аккаунт");
            System.out.println("6. Выход");
            
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline
            
            switch (choice) {
                case 1:
                    showRecordsMenu();
                    break;
                case 2:
                    startNewGame();
                    break;
                case 3:
                    loadGame();
                    break;
                case 4:
                    MapEditor editor = new MapEditor();
                    editor.start();
                    break;
                case 5:
                    handleAccountLogin();
                    break;
                case 6:
                    System.out.println("\nТаблица рекордов перед выходом:");
                    highScoreManager.displayHighScores();
                    System.out.println("\nДо свидания!");
                    System.exit(0);
                default:
                    System.out.println("Неверный выбор!");
            }
        }
    }

    private static void handleAccountLogin() {
        while (true) {
            System.out.println("\n=== Управление аккаунтом ===");
            System.out.println("1. Войти в существующий аккаунт");
            System.out.println("2. Создать новый аккаунт");
            System.out.println("3. Выйти из текущего аккаунта");
            System.out.print("Выберите действие: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    if (loginAccount()) {
                        return;
                    }
                    break;
                case 2:
                    if (registerAccount()) {
                        return;
                    }
                    break;
                case 3:
                    accountManager.logout();
                    currentAccount = null;
                    System.out.println("Вы вышли из аккаунта.");
                    break;
                default:
                    System.out.println("Неверный выбор!");
            }
        }
    }

    private static boolean loginAccount() {
        System.out.print("Введите имя пользователя: ");
        String username = scanner.nextLine();
        System.out.print("Введите пароль: ");
        String password = scanner.nextLine();

        if (accountManager.login(username, password)) {
            currentAccount = accountManager.getCurrentAccount().orElse(null);
            System.out.println("Успешный вход в аккаунт " + username);
            if (currentAccount != null && currentAccount.isInfected()) {
                System.out.println("\n⚠️ ВНИМАНИЕ: Ваш аккаунт заражен вирусом! ⚠️");
                System.out.println("Количество вирусов: " + currentAccount.getViruses().size());
                for (Virus virus : currentAccount.getViruses()) {
                    System.out.println("  - Вирус: " + virus.getName() + " (ID: " + virus.getId() + ")");
                }
            }
            return true;
        } else {
            System.out.println("Неверное имя пользователя или пароль!");
            return false;
        }
    }

    private static boolean registerAccount() {
        System.out.print("Введите имя пользователя: ");
        String username = scanner.nextLine();
        System.out.print("Введите пароль: ");
        String password = scanner.nextLine();

        if (accountManager.register(username, password)) {
            if (accountManager.login(username, password)) {
                currentAccount = accountManager.getCurrentAccount().orElse(null);
                System.out.println("Аккаунт " + username + " успешно создан и активирован!");
                return true;
            }
        } else {
            System.out.println("Аккаунт с таким именем уже существует!");
        }
        return false;
    }

    private static void showRecordsMenu() {
        while (true) {
            System.out.println("\n=== Меню рекордов ===");
            System.out.println("1. Показать все рекорды");
            System.out.println("2. Показать рекорды по карте");
            System.out.println("3. Показать мои рекорды");
            System.out.println("4. Вернуться в главное меню");
            System.out.print("Выберите действие: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline
            
            switch (choice) {
                case 1:
                    System.out.println("\n=== ОБЩАЯ ТАБЛИЦА РЕКОРДОВ ===");
                    highScoreManager.displayHighScores();
                    break;
                case 2:
                    showRecordsByMap();
                    break;
                case 3:
                    if (currentAccount != null && !currentAccount.getUsername().trim().isEmpty()) {
                        showPlayerRecords();
                    } else {
                        System.out.println("Для просмотра личных рекордов необходимо начать игру.");
                        System.out.println("Выберите пункт \"Новая игра\" в главном меню.");
                    }
                    break;
                case 4:
                    return;
                default:
                    System.out.println("Неверный выбор. Попробуйте снова.");
            }
            
            System.out.println("\nНажмите Enter для продолжения...");
            scanner.nextLine();
        }
    }

    private static void showRecordsByMap() {
        List<String> maps = MapEditor.getAvailableMaps();
        if (maps.isEmpty()) {
            System.out.println("Нет доступных карт с рекордами.");
            return;
        }

        System.out.println("\nДоступные карты:");
        for (int i = 0; i < maps.size(); i++) {
            System.out.println((i + 1) + ". " + maps.get(i).replace(".map", ""));
        }

        System.out.print("\nВыберите номер карты: ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // consume newline

        if (choice < 1 || choice > maps.size()) {
            System.out.println("Неверный выбор карты!");
            return;
        }

        String mapName = maps.get(choice - 1).replace(".map", "");
        highScoreManager.displayMapHighScores(mapName);
    }

    private static void showPlayerRecords() {
        System.out.println("\n=== Рекорды игрока " + currentAccount.getUsername() + " ===\n");
        highScoreManager.displayPlayerHighScores(currentAccount.getUsername());
    }

    private static void startNewGame() {
        if (currentAccount == null) {
            System.out.println("Сначала войдите в аккаунт!");
            return;
        }

        GameMap gameMap = selectMap();
        if (gameMap == null) {
            return;
        }

        // Создаем игрока
        Player player = new Player(currentAccount.getUsername(), 1000);

        // Создаем остальные объекты игры
        HeroCastle heroCastle = new HeroCastle(gameMap.getHeight(), gameMap.getWidth());
        EnemyCastle enemyCastle = new EnemyCastle(gameMap.getHeight(), gameMap.getWidth());
        Hero hero = new Hero("Hero", 10, Team.HERO, 100, gameMap.getWidth(), gameMap.getHeight(), 100, 10, 5, 3, new ArrayList<>());
        Enemy enemy = new Enemy("Enemy", 5, Team.ENEMY, 50, gameMap.getWidth(), gameMap.getHeight(), 50, 8, 3, 2, new ArrayList<>());
        Road road = new Road(0, gameMap.getHeight() / 2, gameMap.getWidth() - 1, gameMap.getHeight() / 2);
        Carriage carriage = new Carriage(new Position(gameMap.getWidth() / 2, gameMap.getHeight() / 2), 1, 5, Carriage.Direction.RIGHT);
        List<Unit> allUnits = new ArrayList<>();
        allUnits.addAll(hero.getUnits());
        allUnits.addAll(enemy.getUnits());

        // Создаем GameState с учетом состояния заражения текущего аккаунта
        System.out.println("\n=== Состояние аккаунта при создании новой игры ===");
        System.out.println("Заражен: " + currentAccount.isInfected());
        System.out.println("Количество вирусов: " + currentAccount.getViruses().size());
        for (Virus virus : currentAccount.getViruses()) {
            System.out.println("  - Вирус: " + virus.getName() + " (ID: " + virus.getId() + ")");
        }

        GameState gameState = new GameState(
            currentAccount.getUsername(),
            player,
            gameMap,
            hero,
            enemy,
            heroCastle,
            enemyCastle,
            allUnits,
            carriage,
            road,
            currentAccount.isInfected(),
            currentAccount.getViruses()
        );

        List<Unit> buyUnit = new ArrayList<>();
        Unit warrior1 = new Unit(WARRIOR, 100, 100, 1, 10, Team.HERO, 'W', 100);
        Unit warrior2 = new Unit(WARRIOR, 100, 100, 1, 10, Team.HERO, 'W', 100);
        buyUnit.add(warrior1);
        buyUnit.add(warrior2);

        MapManager mapManager = new MapManager(
            gameState.getHeroCastle(),
            gameState.getEnemyCastle(),
            gameState.getEnemy(),
            gameState.getHero(),
            gameState.getGameMap(),
            gameState.getRoad(),
            gameState.getCarriage(),
            gameState.isAccountInfected(),
            gameState.getAccountViruses()
        );

        // Создаем игру и устанавливаем ее в MapManager
        Game game = new Game(accountManager, gameMap);
        game.setMapManager(mapManager);
        mapManager.setGame(game);

        // Входим в замок
        CastleManager.enterCastle(
            gameState.getHeroCastle(),
            gameState.getHero(),
            gameState.getPlayer(),
            gameState.getEnemy(),
            gameState.getEnemyCastle(),
            gameState.getHeroCastle(),
            gameState.getGameMap(),
            mapManager,
            buyUnit,
            gameState.getHero(),
            new BattleField(gameState.getAllUnits()),
            gameState.getAllUnits(),
            gameState.getCarriage(),
            scanner
        );

        // После выхода из замка запускаем игровой цикл
        startGameLoop(gameState);
    }

    private static void loadGame() {
        if (currentAccount == null) {
            System.out.println("Сначала войдите в аккаунт!");
            return;
        }

        List<String> saves = saveManager.getAvailableSaves(currentAccount.getUsername());
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
        GameState gameState = saveManager.loadGame(currentAccount.getUsername(), saveName);
        if (gameState != null) {
            // Синхронизируем флаги построек с замком героя
            CastleManager.syncBuildingFlags(gameState.getHeroCastle());
            // Синхронизируем флаги покупки юнитов и героя
            CastleManager.syncUnitFlags(gameState.getHero());

            // Создаем buyUnit и mapManager для передачи в enterCastle
            List<Unit> buyUnit = new ArrayList<>();
            Unit warrior1 = new Unit(WARRIOR, 100, 100, 1, 10, Team.HERO, 'W', 100);
            Unit warrior2 = new Unit(WARRIOR, 100, 100, 1, 10, Team.HERO, 'W', 100);
            buyUnit.add(warrior1);
            buyUnit.add(warrior2);

            MapManager mapManager = new MapManager(
                gameState.getHeroCastle(),
                gameState.getEnemyCastle(),
                gameState.getEnemy(),
                gameState.getHero(),
                gameState.getGameMap(),
                gameState.getRoad(),
                gameState.getCarriage(),
                gameState.isAccountInfected(),
                gameState.getAccountViruses()
            );

            // Проверяем, находится ли герой в замке
            Position heroPos = gameState.getHero().getPosition();
            Position heroCastlePos = gameState.getHeroCastle().getPosition();
            Position enemyCastlePos = gameState.getEnemyCastle().getPosition();

            if ((heroPos.getX() == heroCastlePos.getX() && heroPos.getY() == heroCastlePos.getY()) ||
                (heroPos.getX() == enemyCastlePos.getX() && heroPos.getY() == enemyCastlePos.getY())) {
                // Герой в замке — открываем меню замка
                CastleManager.enterCastle(
                    gameState.getHeroCastle(),
                    gameState.getHero(),
                    gameState.getPlayer(),
                    gameState.getEnemy(),
                    gameState.getEnemyCastle(),
                    gameState.getHeroCastle(),
                    gameState.getGameMap(),
                    mapManager,
                    buyUnit,
                    gameState.getHero(),
                    new BattleField(gameState.getAllUnits()),
                    gameState.getAllUnits(),
                    gameState.getCarriage(),
                    scanner
                );
            } else {
                // Герой не в замке — сразу запускаем игровой цикл
                startGameLoop(gameState);
            }
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
            String mapName = maps.get(mapChoice - 1).replace(".map", "");
            GameMap gameMap = MapManager.loadMap(mapName);
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

    private static GameState initializeGameState(Player player, GameMap gameMap, Account currentAccount) {
        Unit warrior = new Unit(WARRIOR, 100, 100, 1, 10, Team.HERO, 'W', 100);
        List<Unit> unitsHero = new ArrayList<>();
        unitsHero.add(warrior);
        unitsHero.add(warrior);

        Unit enemyUnit = new Unit(WARRIOR, 100, 100, 1, 10, Team.ENEMY, 'A', 100);
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

        // Create new GameState with account's infection state
        boolean isInfected = currentAccount != null && currentAccount.isInfected();
        List<Virus> viruses = currentAccount != null ? new ArrayList<>(currentAccount.getViruses()) : new ArrayList<>();
        
        System.out.println("\n=== Создание новой игры для " + player.getName() + " ===");
        System.out.println("Состояние заражения аккаунта:");
        System.out.println("Заражен: " + isInfected);
        System.out.println("Количество вирусов: " + viruses.size());
        for (Virus virus : viruses) {
            System.out.println("  - Вирус: " + virus.getName() + " (ID: " + virus.getId() + ")");
        }

        return new GameState(
            player.getName(),
            player,
            gameMap,
            hero,
            enemy,
            heroCastle,
            enemyCastle,
            allUnits,
            carriage,
            road,
            isInfected,
            viruses
        );
    }

    private static void startGameLoop(GameState gameState) {
        BattleField battleField = new BattleField(gameState.getAllUnits());
        MapManager mapManager = new MapManager(
            gameState.getHeroCastle(),
            gameState.getEnemyCastle(), 
            gameState.getEnemy(),
            gameState.getHero(),
            gameState.getGameMap(),
            gameState.getRoad(),
            gameState.getCarriage(),
            gameState.isAccountInfected(),
            gameState.getAccountViruses()
        );

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
                    Main.startGame(gameState, scanner);
                    return;
                case 2:
                    saveManager.saveGame(currentAccount.getUsername(), gameState, false);
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