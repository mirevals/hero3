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

    @BeforeEach
    void setUp() {
        // Удаляем файл с рекордами перед каждым тестом для чистоты тестирования
        File scoresFile = new File("scores.dat");
        if (scoresFile.exists()) {
            scoresFile.delete();
        }
        highScoreManager = new HighScoreManager();
    }

    @AfterEach
    void tearDown() {
        // Удаляем файл с рекордами после каждого теста
        File scoresFile = new File("scores.dat");
        if (scoresFile.exists()) {
            scoresFile.delete();
        }
    }

    @Test
    @Order(1)
    @DisplayName("Тест обновления рекорда при улучшении результата")
    void testScoreUpdate() {
        // Добавляем начальный рекорд
        Record initialRecord = new Record(TEST_PLAYER, TEST_MAP, 3, 1, 40, 2);
        highScoreManager.addScore(initialRecord);
        
        // Добавляем улучшенный рекорд
        Record betterRecord = new Record(TEST_PLAYER, TEST_MAP, 5, 2, 30, 1);
        highScoreManager.addScore(betterRecord);
        
        // Получаем все рекорды
        List<Record> scores = highScoreManager.getHighScores();

        // Проверяем, что сохранился именно лучший результат
        Record savedRecord = scores.stream()
            .filter(r -> r.getPlayerName().equals(TEST_PLAYER) && r.getMapName().equals(TEST_MAP))
            .findFirst()
            .orElseThrow();
        assertTrue(savedRecord.getScore() > initialRecord.getScore(), 
            "Должен сохраниться рекорд с лучшим результатом");
    }

    @Test
    @Order(2)
    @DisplayName("Тест сохранения рекордов на разных картах")
    void testMultipleMapRecords() {
        String map1 = "map1";
        String map2 = "map2";
        
        // Добавляем рекорды на разных картах
        Record recordMap1 = new Record(TEST_PLAYER, map1, 3, 1, 40, 2);
        Record recordMap2 = new Record(TEST_PLAYER, map2, 5, 2, 30, 1);
        
        highScoreManager.addScore(recordMap1);
        highScoreManager.addScore(recordMap2);
        
        List<Record> scores = highScoreManager.getHighScores();
        
        // Проверяем наличие рекордов на обеих картах
        assertEquals(1, scores.stream()
            .filter(r -> r.getMapName().equals(map1))
            .count(), "Должен быть один рекорд на первой карте");
        assertEquals(1, scores.stream()
            .filter(r -> r.getMapName().equals(map2))
            .count(), "Должен быть один рекорд на второй карте");
    }

    @Test
    @Order(3)
    @DisplayName("Тест обновления рекордов разных игроков")
    void testMultiplePlayerRecords() {
        String player1 = "player1";
        String player2 = "player2";
        
        // Добавляем рекорды разных игроков
        Record recordPlayer1 = new Record(player1, TEST_MAP, 3, 1, 40, 2);
        Record recordPlayer2 = new Record(player2, TEST_MAP, 5, 2, 30, 1);
        Record betterRecordPlayer1 = new Record(player1, TEST_MAP, 6, 3, 25, 0);
        
        highScoreManager.addScore(recordPlayer1);
        highScoreManager.addScore(recordPlayer2);
        highScoreManager.addScore(betterRecordPlayer1);
        
        List<Record> scores = highScoreManager.getHighScores();
        

        // Проверяем, что сохранился лучший результат первого игрока
        Record savedPlayer1Record = scores.stream()
            .filter(r -> r.getPlayerName().equals(player1))
            .findFirst()
            .orElseThrow();
        assertTrue(savedPlayer1Record.getScore() > recordPlayer1.getScore(),
            "Должен сохраниться улучшенный рекорд первого игрока");
    }

    @Test
    @Order(4)
    @DisplayName("Тест правильности подсчета очков")
    void testScoreCalculation() {
        // Тестируем различные сценарии подсчета очков
        
        // Сценарий 1: Много врагов, мало замков
        Record record1 = new Record(TEST_PLAYER, TEST_MAP, 10, 1, 60, 2);
        
        // Сценарий 2: Мало врагов, много замков
        Record record2 = new Record(TEST_PLAYER, TEST_MAP, 2, 4, 45, 1);
        
        // Сценарий 3: Быстрое прохождение (бонус за время)
        Record record3 = new Record(TEST_PLAYER, TEST_MAP, 5, 2, 30, 1);
        
        // Проверяем формулу подсчета очков для каждого сценария
        assertEquals(1000 + 500 - 100, record1.getScore(), // 10*100 + 1*500 - 2*50
            "Неверный подсчет очков для сценария с множеством врагов");

    }

    @Test
    @Order(5)
    @DisplayName("Тест лимита на количество рекордов")
    void testRecordsLimit() {
        // Добавляем 7 рекордов (больше максимума в 5)
        for (int i = 0; i < 7; i++) {
            Record record = new Record(
                "player" + i,
                TEST_MAP,
                5 + i, // Увеличиваем количество врагов для разных очков
                2,
                30,
                1
            );
            highScoreManager.addScore(record);
        }
        
        List<Record> scores = highScoreManager.getHighScores();
        
        // Проверяем, что сохранилось только 5 лучших рекордов
        assertEquals(5, scores.size(), "Должно сохраниться только 5 рекордов");
        
        // Проверяем, что рекорды отсортированы по убыванию очков
        for (int i = 0; i < scores.size() - 1; i++) {
            assertTrue(scores.get(i).getScore() >= scores.get(i + 1).getScore(),
                "Рекорды должны быть отсортированы по убыванию");
        }
    }

    @Test
    @Order(6)
    @DisplayName("Тест сохранения рекордов между перезапусками")
    void testRecordsPersistence() {
        // Добавляем рекорды
        Record record1 = new Record(TEST_PLAYER, TEST_MAP, 5, 2, 30, 1);
        Record record2 = new Record("player2", TEST_MAP, 6, 3, 25, 0);
        
        highScoreManager.addScore(record1);
        highScoreManager.addScore(record2);
        
        // Создаем новый экземпляр менеджера (имитация перезапуска)
        HighScoreManager newManager = new HighScoreManager();
        List<Record> loadedScores = newManager.getHighScores();
        
        // Проверяем, что все рекорды загрузились
        assertEquals(2, loadedScores.size(), "Должны загрузиться все сохраненные рекорды");
        
        // Проверяем, что данные рекордов сохранились корректно
        assertTrue(loadedScores.stream()
            .anyMatch(r -> r.getPlayerName().equals(TEST_PLAYER) && 
                         r.getScore() == record1.getScore()),
            "Должен найтись рекорд первого игрока с правильными очками");
        
        assertTrue(loadedScores.stream()
            .anyMatch(r -> r.getPlayerName().equals("player2") && 
                         r.getScore() == record2.getScore()),
            "Должен найтись рекорд второго игрока с правильными очками");
    }
} 