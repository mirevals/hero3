package org.example.game;

import org.example.game.score.HighScoreManager;
import org.example.game.score.Record;
import java.util.Scanner;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

// ... existing code ...

public class Game {
    private GameState gameState;
    private HighScoreManager highScoreManager;
    private String playerName;
    private Scanner scanner;
    
    public Game() {
        this.highScoreManager = new HighScoreManager();
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        showMainMenu();
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
        gameState = new GameState();
        gameState.setCurrentMapName(mapName);
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
            Record record = gameState.generateRecord(playerName);
            highScoreManager.addScore(record);
            System.out.println("\nВаш результат:");
            System.out.println("Уничтожено врагов: " + gameState.getEnemiesDefeated());
            System.out.println("Захвачено замков: " + gameState.getCastlesCaptured());
            System.out.println("Потеряно юнитов: " + gameState.getUnitsLost());
            System.out.println("Количество ходов: " + gameState.getTurnsCompleted());
            System.out.println("\nТаблица рекордов:");
            highScoreManager.displayHighScores();
        }
    }

    // Вызывать этот метод после каждого хода
    private void updateGameStatistics() {
        gameState.incrementTurns();
    }

    // Вызывать при уничтожении вражеского юнита
    private void onEnemyDefeated() {
        gameState.incrementEnemiesDefeated();
    }

    // Вызывать при захвате замка
    private void onCastleCaptured() {
        gameState.incrementCastlesCaptured();
    }

    // Вызывать при потере союзного юнита
    private void onUnitLost() {
        gameState.incrementUnitsLost();
    }

    // ... existing code ...
} 