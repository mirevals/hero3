package org.example.game.map;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.io.File;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MapEditorValidationTest {
    private static final int MAP_WIDTH = 10;
    private static final int MAP_HEIGHT = 10;
    private static final String TEST_MAP_NAME = "test_map.map";
    private GameMap gameMap;
    private MapEditor mapEditor;

    @BeforeEach
    void setUp() {
        gameMap = new GameMap(MAP_WIDTH, MAP_HEIGHT);
        mapEditor = new MapEditor();
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
    @DisplayName("Тест валидации размеров карты")
    void testMapSizeValidation() {
        // Проверка на отрицательные размеры
        assertThrows(IllegalArgumentException.class, () -> new GameMap(-1, 10),
                "Карта не должна создаваться с отрицательной шириной");
        assertThrows(IllegalArgumentException.class, () -> new GameMap(10, -1),
                "Карта не должна создаваться с отрицательной высотой");
        
        // Проверка на нулевые размеры
        assertThrows(IllegalArgumentException.class, () -> new GameMap(0, 10),
                "Карта не должна создаваться с нулевой шириной");
        assertThrows(IllegalArgumentException.class, () -> new GameMap(10, 0),
                "Карта не должна создаваться с нулевой высотой");
        
        // Проверка валидных размеров
        GameMap validMap = new GameMap(10, 10);
        assertEquals(10, validMap.getWidth(), "Ширина карты должна быть 10");
        assertEquals(10, validMap.getHeight(), "Высота карты должна быть 10");
    }

    @Test
    @Order(2)
    @DisplayName("Тест валидации координат при установке значений ячеек")
    void testCoordinateValidation() {
        // Проверка выхода за границы карты
        assertThrows(IllegalArgumentException.class, () -> gameMap.setCellValue(-1, 5, '.'),
                "Нельзя установить значение по отрицательной X координате");
        assertThrows(IllegalArgumentException.class, () -> gameMap.setCellValue(5, -1, '.'),
                "Нельзя установить значение по отрицательной Y координате");
        assertThrows(IllegalArgumentException.class, () -> gameMap.setCellValue(MAP_WIDTH, 5, '.'),
                "Нельзя установить значение за пределами ширины карты");
        assertThrows(IllegalArgumentException.class, () -> gameMap.setCellValue(5, MAP_HEIGHT, '.'),
                "Нельзя установить значение за пределами высоты карты");

        // Проверка установки значения в допустимые координаты
        assertDoesNotThrow(() -> gameMap.setCellValue(5, 5, '.'),
                "Должна быть возможность установить значение в пределах карты");
    }

    @Test
    @Order(3)
    @DisplayName("Тест валидации типов местности")
    void testTerrainTypeValidation() {
        // Проверка допустимых типов местности
        assertDoesNotThrow(() -> gameMap.setCellValue(1, 1, '.'), "Точка (дорога) должна быть допустимым типом местности");
        assertDoesNotThrow(() -> gameMap.setCellValue(2, 2, '#'), "Стена должна быть допустимым типом местности");
        assertDoesNotThrow(() -> gameMap.setCellValue(3, 3, ' '), "Пустое пространство должно быть допустимым типом местности");
        
        // Проверка получения значений
        assertEquals('.', gameMap.getCellValue(1, 1), "Значение ячейки должно быть '.'");
        assertEquals('#', gameMap.getCellValue(2, 2), "Значение ячейки должно быть '#'");
        assertEquals(' ', gameMap.getCellValue(3, 3), "Значение ячейки должно быть пробелом");
    }

    @Test
    @Order(4)
    @DisplayName("Тест проверки границ карты")
    void testMapBoundaries() {
        // Заполняем границы карты стенами
        for (int x = 0; x < MAP_WIDTH; x++) {
            gameMap.setCellValue(x, 0, '#');
            gameMap.setCellValue(x, MAP_HEIGHT - 1, '#');
        }
        for (int y = 0; y < MAP_HEIGHT; y++) {
            gameMap.setCellValue(0, y, '#');
            gameMap.setCellValue(MAP_WIDTH - 1, y, '#');
        }

        // Проверяем границы
        for (int x = 0; x < MAP_WIDTH; x++) {
            assertEquals('#', gameMap.getCellValue(x, 0), "Верхняя граница должна быть стеной");
            assertEquals('#', gameMap.getCellValue(x, MAP_HEIGHT - 1), "Нижняя граница должна быть стеной");
        }
        for (int y = 0; y < MAP_HEIGHT; y++) {
            assertEquals('#', gameMap.getCellValue(0, y), "Левая граница должна быть стеной");
            assertEquals('#', gameMap.getCellValue(MAP_WIDTH - 1, y), "Правая граница должна быть стеной");
        }
    }

    @Test
    @Order(5)
    @DisplayName("Тест изменения размера карты")
    void testMapResize() {
        // Заполняем исходную карту
        for (int y = 0; y < MAP_HEIGHT; y++) {
            for (int x = 0; x < MAP_WIDTH; x++) {
                gameMap.setCellValue(x, y, '.');
            }
        }

        // Создаем новую карту большего размера
        GameMap newMap = new GameMap(MAP_WIDTH + 5, MAP_HEIGHT + 5);
        
        // Копируем содержимое старой карты
        for (int y = 0; y < MAP_HEIGHT; y++) {
            for (int x = 0; x < MAP_WIDTH; x++) {
                newMap.setCellValue(x, y, gameMap.getCellValue(x, y));
            }
        }

        // Проверяем, что старое содержимое сохранилось
        for (int y = 0; y < MAP_HEIGHT; y++) {
            for (int x = 0; x < MAP_WIDTH; x++) {
                assertEquals(gameMap.getCellValue(x, y), newMap.getCellValue(x, y),
                        "Содержимое ячейки должно быть скопировано в новую карту");
            }
        }

        // Проверяем новые размеры
        assertEquals(MAP_WIDTH + 5, newMap.getWidth(), "Новая ширина карты должна быть увеличена на 5");
        assertEquals(MAP_HEIGHT + 5, newMap.getHeight(), "Новая высота карты должна быть увеличена на 5");
    }

    @Test
    @Order(6)
    @DisplayName("Тест проверки связности дороги")
    void testRoadConnectivity() {
        // Создаем дорогу от (1,1) до (8,1)
        Road road = new Road(1, 1, 8, 1);

        // Проверяем, что все точки дороги доступны
        for (int x = 1; x <= 8; x++) {
            assertTrue(road.isPointOnRoad(x, 1),
                    String.format("Точка (%d, 1) должна быть частью дороги", x));
        }

        // Проверяем точки вне дороги
        assertFalse(road.isPointOnRoad(0, 1), "Точка (0, 1) не должна быть частью дороги");
        assertFalse(road.isPointOnRoad(9, 1), "Точка (9, 1) не должна быть частью дороги");
        assertFalse(road.isPointOnRoad(5, 0), "Точка (5, 0) не должна быть частью дороги");
        assertFalse(road.isPointOnRoad(5, 2), "Точка (5, 2) не должна быть частью дороги");
    }

    @Nested
    @DisplayName("Тесты сохранения и загрузки")
    class SaveLoadTests {
        @Test
        @DisplayName("Сохранение и загрузка пустой карты")
        void testSaveLoadEmptyMap() {
            MapManager.saveMap(gameMap, TEST_MAP_NAME);
            assertTrue(new File("maps/" + TEST_MAP_NAME).exists());
            
            GameMap loadedMap = MapManager.loadMap(TEST_MAP_NAME);
            assertNotNull(loadedMap);
            assertEquals(gameMap.getWidth(), loadedMap.getWidth());
            assertEquals(gameMap.getHeight(), loadedMap.getHeight());
        }

        @Test
        @DisplayName("Сохранение и загрузка карты с объектами")
        void testSaveLoadMapWithObjects() {
            gameMap.setCellValue(1, 1, 'C');
            gameMap.setCellValue(2, 2, 'R');
            gameMap.setCellValue(3, 3, '#');
            
            MapManager.saveMap(gameMap, TEST_MAP_NAME);
            GameMap loadedMap = MapManager.loadMap(TEST_MAP_NAME);
            
            assertEquals('C', loadedMap.getCellValue(1, 1));
            assertEquals('R', loadedMap.getCellValue(2, 2));
            assertEquals('#', loadedMap.getCellValue(3, 3));
        }

        @Test
        @DisplayName("Загрузка несуществующей карты")
        void testLoadNonexistentMap() {
            assertThrows(RuntimeException.class, () -> 
                MapManager.loadMap("nonexistent_map.map"),
                "Загрузка несуществующей карты должна вызывать RuntimeException");
        }

        @Test
        @DisplayName("Сохранение с некорректным именем файла")
        void testSaveWithInvalidFileName() {
            assertThrows(IllegalArgumentException.class, () -> 
                MapManager.saveMap(gameMap, "invalid/file/name.map"),
                "Сохранение с некорректным именем файла должно вызывать IllegalArgumentException");
        }

        @Test
        @DisplayName("Сохранение с пустым именем файла")
        void testSaveWithEmptyFileName() {
            assertThrows(IllegalArgumentException.class, () -> 
                MapManager.saveMap(gameMap, ""),
                "Сохранение с пустым именем файла должно вызывать IllegalArgumentException");
        }

        @Test
        @DisplayName("Сохранение null карты")
        void testSaveNullMap() {
            assertThrows(IllegalArgumentException.class, () -> 
                MapManager.saveMap(null, TEST_MAP_NAME),
                "Сохранение null карты должно вызывать IllegalArgumentException");
        }
    }
} 