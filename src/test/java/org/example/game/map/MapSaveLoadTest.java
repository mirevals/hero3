package org.example.game.map;

import org.junit.jupiter.api.*;
import java.io.File;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MapSaveLoadTest {
    private static final String TEST_MAP_NAME = "test_map.map";
    private static final int MAP_WIDTH = 10;
    private static final int MAP_HEIGHT = 10;
    private GameMap gameMap;
    private MapSaveManager mapSaveManager;

    @BeforeEach
    void setUp() {
        gameMap = new GameMap(MAP_WIDTH, MAP_HEIGHT);
        mapSaveManager = new MapSaveManager();
        // Создаем директорию для карт, если её нет
        new File("maps").mkdirs();
    }

    @AfterEach
    void tearDown() {
        // Удаляем тестовую карту после каждого теста
        File testMap = new File("maps/" + TEST_MAP_NAME);
        if (testMap.exists()) {
            testMap.delete();
        }
    }

    @Test
    @Order(1)
    @DisplayName("Тест сохранения пустой карты")
    void testSaveEmptyMap() {
        // Сохраняем пустую карту
        assertTrue(mapSaveManager.saveMap(gameMap, TEST_MAP_NAME),
                "Сохранение пустой карты должно быть успешным");
        
        // Проверяем, что файл создан
        File savedMap = new File("maps/" + TEST_MAP_NAME);
        assertTrue(savedMap.exists(), "Файл карты должен существовать после сохранения");
        assertTrue(savedMap.length() > 0, "Файл карты не должен быть пустым");
    }

    @Test
    @Order(2)
    @DisplayName("Тест сохранения карты с различными типами местности")
    void testSaveMapWithTerrain() {
        // Заполняем карту различными типами местности
        char[][] terrainTypes = {
            {'#', '.', 'C', 'R'},  // стена, дорога, замок, равнина
            {'P', 'F', 'M', 'W'}   // равнина, лес, горы, вода
        };

        for (int y = 0; y < terrainTypes.length; y++) {
            for (int x = 0; x < terrainTypes[y].length; x++) {
                gameMap.setCellValue(x, y, terrainTypes[y][x]);
            }
        }

        // Сохраняем карту
        assertTrue(mapSaveManager.saveMap(gameMap, TEST_MAP_NAME),
                "Сохранение карты с местностью должно быть успешным");

        // Загружаем карту и проверяем содержимое
        GameMap loadedMap = mapSaveManager.loadMap(TEST_MAP_NAME);
        assertNotNull(loadedMap, "Загруженная карта не должна быть null");

        // Проверяем, что все типы местности сохранились корректно
        for (int y = 0; y < terrainTypes.length; y++) {
            for (int x = 0; x < terrainTypes[y].length; x++) {
                assertEquals(terrainTypes[y][x], loadedMap.getCellValue(x, y),
                        String.format("Тип местности в позиции (%d,%d) должен совпадать", x, y));
            }
        }
    }

    @Test
    @Order(3)
    @DisplayName("Тест сохранения карты с дорогой")
    void testSaveMapWithRoad() {
        // Создаем дорогу
        Road road = new Road(1, 1, 8, 1);
        road.placeRoad(gameMap.getMap());

        // Сохраняем карту
        assertTrue(mapSaveManager.saveMap(gameMap, TEST_MAP_NAME),
                "Сохранение карты с дорогой должно быть успешным");

        // Загружаем карту и проверяем дорогу
        GameMap loadedMap = mapSaveManager.loadMap(TEST_MAP_NAME);
        assertNotNull(loadedMap, "Загруженная карта не должна быть null");

        // Проверяем все точки дороги
        for (int x = 1; x <= 8; x++) {
            assertEquals('.', loadedMap.getCellValue(x, 1),
                    String.format("Точка дороги в позиции (%d,1) должна быть '.'", x));
        }
    }

    @Test
    @Order(4)
    @DisplayName("Тест сохранения карты с замками")
    void testSaveMapWithCastles() {
        // Размещаем замки на карте
        gameMap.setCellValue(1, 1, 'C'); // Замок героя
        gameMap.setCellValue(8, 8, 'E'); // Замок врага

        // Сохраняем карту
        assertTrue(mapSaveManager.saveMap(gameMap, TEST_MAP_NAME),
                "Сохранение карты с замками должно быть успешным");

        // Загружаем карту и проверяем замки
        GameMap loadedMap = mapSaveManager.loadMap(TEST_MAP_NAME);
        assertNotNull(loadedMap, "Загруженная карта не должна быть null");

        // Проверяем позиции замков
        assertEquals('C', loadedMap.getCellValue(1, 1), "Замок героя должен быть в позиции (1,1)");
        assertEquals('E', loadedMap.getCellValue(8, 8), "Замок врага должен быть в позиции (8,8)");
    }

    @Test
    @Order(5)
    @DisplayName("Тест сохранения карты максимального размера")
    void testSaveMaxSizeMap() {
        // Создаем карту большого размера
        GameMap largeMap = new GameMap(100, 100);
        
        // Заполняем карту разными типами местности
        for (int y = 0; y < 100; y++) {
            for (int x = 0; x < 100; x++) {
                largeMap.setCellValue(x, y, (x + y) % 2 == 0 ? '.' : '#');
            }
        }

        // Сохраняем карту
        assertTrue(mapSaveManager.saveMap(largeMap, TEST_MAP_NAME),
                "Сохранение большой карты должно быть успешным");

        // Загружаем карту и проверяем размеры
        GameMap loadedMap = mapSaveManager.loadMap(TEST_MAP_NAME);
        assertNotNull(loadedMap, "Загруженная карта не должна быть null");
        assertEquals(100, loadedMap.getWidth(), "Ширина загруженной карты должна быть 100");
        assertEquals(100, loadedMap.getHeight(), "Высота загруженной карты должна быть 100");

        // Проверяем содержимое
        for (int y = 0; y < 100; y++) {
            for (int x = 0; x < 100; x++) {
                assertEquals((x + y) % 2 == 0 ? '.' : '#', loadedMap.getCellValue(x, y),
                        String.format("Неверное значение в позиции (%d,%d)", x, y));
            }
        }
    }

    @Test
    @Order(6)
    @DisplayName("Тест обработки ошибок при сохранении")
    void testSaveErrorHandling() {
        // Попытка сохранения null карты
        assertThrows(IllegalArgumentException.class, () -> mapSaveManager.saveMap(null, TEST_MAP_NAME),
                "Сохранение null карты должно вызывать исключение");

        // Попытка сохранения с пустым именем файла
        assertThrows(IllegalArgumentException.class, () -> mapSaveManager.saveMap(gameMap, ""),
                "Сохранение с пустым именем файла должно вызывать исключение");

        // Попытка сохранения с некорректным именем файла
        assertThrows(IllegalArgumentException.class, () -> mapSaveManager.saveMap(gameMap, "invalid/file/name"),
                "Сохранение с некорректным именем файла должно вызывать исключение");
    }
} 