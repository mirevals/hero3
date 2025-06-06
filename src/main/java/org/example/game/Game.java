package org.example.game;

import org.example.game.score.HighScoreManager;
import org.example.game.score.Record;
import org.example.game.build.*;
import org.example.game.map.GameMap;
import org.example.game.map.MapManager;
import org.example.game.person.*;
import org.example.game.time.GameTime;
import org.example.game.battle.BattleField;

import java.util.*;
import java.util.stream.Collectors;

public class Game {
    private final org.example.game.save.GameState saveGameState;
    private final org.example.game.GameState statsGameState;
    private final MapManager mapManager;
    private final Random random;
    private static final int NPC_COUNT = 10; // Number of NPCs in the game
    private HighScoreManager highScoreManager;
    private String playerName;
    private Scanner scanner;
    
    public Game(org.example.game.save.GameState saveGameState, MapManager mapManager) {
        this.saveGameState = saveGameState;
        this.statsGameState = new org.example.game.GameState();
        this.mapManager = mapManager;
        this.random = new Random();
        this.highScoreManager = new HighScoreManager();
        this.scanner = new Scanner(System.in);
        this.playerName = saveGameState.getPlayerName();
    }

    public void start() {
        // Initialize NPCs
        List<org.example.game.person.Character> npcs = initializeNPCs();
        
        // Main game loop
        while (true) {
            // Update game time (1 game minute = 100ms real time)
            GameTime.updateTime(100);
            
            // Update all service buildings
            updateServiceBuildings();
            
            // Process NPC actions
            processNPCActions(npcs);
            
            // Process player turn
            if (!mapManager.startGame(saveGameState.getHero(), saveGameState.getEnemy(), saveGameState.getHeroCastle(),
                saveGameState.getPlayer(), saveGameState.getEnemyCastle(), saveGameState.getHeroCastle(),
                saveGameState.getGameMap(), mapManager, new ArrayList<>(), new BattleField(saveGameState.getAllUnits()),
                saveGameState.getAllUnits(), saveGameState.getCarriage())) {
                break; // Game over
            }
            
            // Update game statistics after each turn
            updateGameStatistics();
            
            try {
                Thread.sleep(100); // 1 game minute = 100ms real time
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
    
    private List<org.example.game.person.Character> initializeNPCs() {
        List<org.example.game.person.Character> npcs = new ArrayList<>();
        for (int i = 0; i < NPC_COUNT; i++) {
            // Create NPCs with random positions and basic stats
            Hero npc = new Hero("NPC " + (i + 1), 5, Team.HERO, 100, 
                              saveGameState.getGameMap().getWidth(), 
                              saveGameState.getGameMap().getHeight(),
                              50, 10, 10, 1, new ArrayList<>());
            npcs.add(npc);
        }
        return npcs;
    }
    
    private void updateServiceBuildings() {
        System.out.println("\n=== Отладка updateServiceBuildings ===");
        System.out.println("Обновление сервисных зданий в замке героя...");
        
        // Update services in all castles
        for (Building building : saveGameState.getHeroCastle().getConstructedBuildings()) {
            if (building instanceof ServiceBuilding) {
                System.out.println("Обновление здания: " + building.getName());
                ((ServiceBuilding) building).updateServices();
            }
        }
        
        System.out.println("Обновление сервисных зданий в замке врага...");
        for (Building building : saveGameState.getEnemyCastle().getConstructedBuildings()) {
            if (building instanceof ServiceBuilding) {
                System.out.println("Обновление здания: " + building.getName());
                ((ServiceBuilding) building).updateServices();
            }
        }
        System.out.println("=== Конец отладки updateServiceBuildings ===\n");
    }
    
    private void processNPCActions(List<org.example.game.person.Character> npcs) {
        for (org.example.game.person.Character npc : npcs) {
            if (npc.isDead()) continue;
            
            // Randomly decide if NPC wants to visit a building
            if (random.nextDouble() < 0.3) { // 30% chance to visit a building
                // Get all service buildings
                List<ServiceBuilding> availableBuildings = new ArrayList<>();
                for (Building building : saveGameState.getHeroCastle().getConstructedBuildings()) {
                    if (building instanceof ServiceBuilding) {
                        availableBuildings.add((ServiceBuilding) building);
                    }
                }
                
                if (!availableBuildings.isEmpty()) {
                    // Choose a random building
                    ServiceBuilding building = availableBuildings.get(random.nextInt(availableBuildings.size()));
                    
                    if (building.canAcceptVisitor()) {
                        // Choose a random service
                        List<?> services = building.getAvailableServices();
                        if (!services.isEmpty()) {
                            int serviceIndex = random.nextInt(services.size());
                            building.startService(npc, serviceIndex);
                            System.out.println(npc.getName() + " начал использовать услугу " + 
                                             services.get(serviceIndex).toString() + 
                                             " в " + building.getName());
                        }
                    }
                }
            }
        }
    }
    
    private void showMainMenu() {
        while (true) {
            System.out.println("\n=== Главное меню ===");
            System.out.println("1. Рекорды");
            System.out.println("2. Новая игра");
            System.out.println("3. Загрузить игру");
            System.out.println("4. Редактор карт");
            System.out.println("5. Выход");
            System.out.print("Выберите действие: ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    showRecordsMenu();
                    break;
                case "2":
                    startNewGame();
                    break;
                case "3":
                    loadGame();
                    break;
                case "4":
                    // ... existing map editor code ...
                    break;
                case "5":
                    System.out.println("\nТаблица рекордов перед выходом:");
                    highScoreManager.displayHighScores();
                    System.out.println("\nСпасибо за игру!");
                    scanner.close();
                    return;
                default:
                    System.out.println("Неверный выбор. Попробуйте снова.");
            }
        }
    }

    private void showRecordsMenu() {
        while (true) {
            System.out.println("\n=== Меню рекордов ===");
            System.out.println("1. Показать все рекорды");
            System.out.println("2. Показать рекорды по карте");
            System.out.println("3. Показать мои рекорды");
            System.out.println("4. Вернуться в главное меню");
            System.out.print("Выберите действие: ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    System.out.println("\n=== ОБЩАЯ ТАБЛИЦА РЕКОРДОВ ===");
                    highScoreManager.displayHighScores();
                    break;
                case "2":
                    showRecordsByMap();
                    break;
                case "3":
                    if (playerName != null && !playerName.trim().isEmpty()) {
                        showPlayerRecords();
                    } else {
                        System.out.println("Для просмотра личных рекордов необходимо начать игру.");
                        System.out.println("Выберите пункт \"Новая игра\" в главном меню.");
                    }
                    break;
                case "4":
                    return;
                default:
                    System.out.println("Неверный выбор. Попробуйте снова.");
            }
            
            System.out.println("\nНажмите Enter для продолжения...");
            scanner.nextLine();
        }
    }

    private void showRecordsByMap() {
        System.out.println("\nДоступные карты:");
        List<Record> allRecords = highScoreManager.getHighScores();
        Set<String> uniqueMaps = new HashSet<>();
        for (Record record : allRecords) {
            uniqueMaps.add(record.getMapName());
        }
        
        if (uniqueMaps.isEmpty()) {
            System.out.println("Пока нет рекордов ни на одной карте.");
            return;
        }

        for (String map : uniqueMaps) {
            System.out.println("- " + map);
        }

        System.out.print("\nВведите название карты: ");
        String mapName = scanner.nextLine();
        
        boolean found = false;
        System.out.println("\n=== Рекорды на карте " + mapName + " ===\n");
        
        for (Record record : allRecords) {
            if (record.getMapName().equalsIgnoreCase(mapName)) {
                System.out.println(record);
                System.out.println("----------------------------------------");
                found = true;
            }
        }
        
        if (!found) {
            System.out.println("Рекордов на этой карте пока нет.");
        }
    }

    private void showPlayerRecords() {
        List<Record> records = highScoreManager.getHighScores();
        boolean found = false;
        
        System.out.println("\n=== Рекорды игрока " + playerName + " ===\n");
        for (Record record : records) {
            if (record.getPlayerName().equalsIgnoreCase(playerName)) {
                System.out.println(record);
                System.out.println("----------------------------------------");
                found = true;
            }
        }
        
        if (!found) {
            System.out.println("У вас пока нет рекордов.");
        }
    }

    private void startNewGame() {
        System.out.print("Введите ваше имя: ");
        playerName = scanner.nextLine();
        
        // Выбор карты
        String mapName = selectMap();
        if (mapName == null) {
            return;
        }
        
        // Инициализация новой игры
        statsGameState.setCurrentMapName(mapName);
        // ... остальной код инициализации ...
    }

    private void loadGame() {
        // Реализация загрузки игры
        System.out.println("Загрузка игры...");
    }

    private String selectMap() {
        // Реализация выбора карты
        System.out.println("Выбор карты...");
        return "default_map"; // Временная заглушка
    }

    private boolean isGameWon() {
        // Реализация проверки победы
        return false; // Временная заглушка
    }

    private void checkGameOver() {
        if (isGameWon()) {
            System.out.println("Поздравляем! Вы победили!");
            Record record = statsGameState.generateRecord(playerName);
            highScoreManager.addScore(record);
            System.out.println("\nВаш результат:");
            System.out.println("Уничтожено врагов: " + statsGameState.getEnemiesDefeated());
            System.out.println("Захвачено замков: " + statsGameState.getCastlesCaptured());
            System.out.println("Потеряно юнитов: " + statsGameState.getUnitsLost());
            System.out.println("Количество ходов: " + statsGameState.getTurnsCompleted());
            System.out.println("\nТаблица рекордов:");
            highScoreManager.displayHighScores();
        }
    }

    // Call this method after each turn
    private void updateGameStatistics() {
        statsGameState.incrementTurns();
    }

    // Call when an enemy unit is defeated
    private void onEnemyDefeated() {
        statsGameState.incrementEnemiesDefeated();
    }

    // Call when a castle is captured
    private void onCastleCaptured() {
        statsGameState.incrementCastlesCaptured();
    }

    // Call when an allied unit is lost
    private void onUnitLost() {
        statsGameState.incrementUnitsLost();
    }

    // ... existing code ...
} 