package org.example.game.score;

import org.junit.jupiter.api.*;
import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class HighScoreManagerTest {
    private static final String TEST_PLAYER = "test_player";
    private static final String TEST_MAP = "test_map";
    private static HighScoreManager highScoreManager;

    @BeforeAll
    static void setUp() {
        highScoreManager = new HighScoreManager();
    }

    @AfterAll
    static void tearDown() {
        // Удаляем файл с рекордами после тестов
        File scoresFile = new File("scores.dat");
        if (scoresFile.exists()) {
            scoresFile.delete();
        }
    }

    @Test
    @Order(1)
    @DisplayName("Тест добавления нового рекорда")
    void testAddNewRecord() {
        Record record = new Record(TEST_PLAYER, TEST_MAP, 5, 2, 30, 1);
        highScoreManager.addScore(record);
        
        List<Record> scores = highScoreManager.getHighScores();
        assertFalse(scores.isEmpty());
        assertEquals(1, scores.size());
        assertEquals(TEST_PLAYER, scores.get(0).getPlayerName());
        assertEquals(TEST_MAP, scores.get(0).getMapName());
    }

    @Test
    @Order(2)
    @DisplayName("Тест сортировки рекордов")
    void testScoreSorting() {
        // Добавляем рекорды с разными очками
        Record lowScore = new Record("player2", TEST_MAP, 1, 0, 40, 2);
        Record highScore = new Record("player3", TEST_MAP, 10, 3, 25, 0);
        
        highScoreManager.addScore(lowScore);
        highScoreManager.addScore(highScore);
        
        List<Record> scores = highScoreManager.getHighScores();
        assertTrue(scores.get(0).getScore() > scores.get(1).getScore());
    }

    @Test
    @Order(3)
    @DisplayName("Тест ограничения количества рекордов")
    void testMaxScoresLimit() {
        // Добавляем больше 5 рекордов
        for (int i = 0; i < 7; i++) {
            Record record = new Record("player" + i, TEST_MAP, i, 1, 30, 0);
            highScoreManager.addScore(record);
        }
        
        List<Record> scores = highScoreManager.getHighScores();
        assertEquals(5, scores.size()); // Проверяем, что хранится только топ-5
    }

    @Test
    @Order(4)
    @DisplayName("Тест сохранения и загрузки рекордов")
    void testScoresPersistence() {
        // Добавляем рекорды в новый менеджер
        HighScoreManager manager = new HighScoreManager();
        for (int i = 0; i < 5; i++) {
            Record record = new Record("player" + i, TEST_MAP, i, 1, 30, 0);
            manager.addScore(record);
        }
        
        // Создаем новый менеджер рекордов (который должен загрузить существующие рекорды)
        HighScoreManager newManager = new HighScoreManager();
        List<Record> scores = newManager.getHighScores();
        
        assertFalse(scores.isEmpty());
        assertEquals(5, scores.size()); // Проверяем, что предыдущие рекорды сохранились
    }

    @Test
    @Order(5)
    @DisplayName("Тест фильтрации рекордов по карте")
    void testMapFiltering() {
        String newMap = "another_map";
        Record recordOnNewMap = new Record(TEST_PLAYER, newMap, 5, 2, 30, 1);
        highScoreManager.addScore(recordOnNewMap);
        
        // Проверяем отображение рекордов для конкретной карты
        highScoreManager.displayMapHighScores(newMap);
        List<Record> allScores = highScoreManager.getHighScores();
        long mapRecordsCount = allScores.stream()
                .filter(r -> r.getMapName().equals(newMap))
                .count();
        assertEquals(1, mapRecordsCount);
    }

    @Test
    @Order(6)
    @DisplayName("Тест обновления рекорда игрока")
    void testPlayerRecordUpdate() {
        // Добавляем новый рекорд для существующего игрока с лучшим результатом
        Record betterRecord = new Record(TEST_PLAYER, TEST_MAP, 15, 5, 20, 0);
        highScoreManager.addScore(betterRecord);
        
        List<Record> scores = highScoreManager.getHighScores();
        Record bestPlayerRecord = scores.stream()
                .filter(r -> r.getPlayerName().equals(TEST_PLAYER))
                .findFirst()
                .orElse(null);
        
        assertNotNull(bestPlayerRecord);
        assertTrue(bestPlayerRecord.getScore() > 1000); // Проверяем, что сохранился лучший результат
    }

    @Test
    @Order(7)
    @DisplayName("Тест расчета очков")
    void testScoreCalculation() {
        // Проверяем формулу подсчета очков
        Record record = new Record(TEST_PLAYER, TEST_MAP, 
            5,  // 5 врагов = 500 очков
            2,  // 2 замка = 1000 очков
            30, // < 50 ходов = 1000 очков бонус
            1   // 1 потерянный юнит = -50 очков
        );
        
        assertEquals(2450, record.getScore()); // 500 + 1000 + 1000 - 50 = 2450
    }
} 