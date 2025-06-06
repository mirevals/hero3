package org.example.map;

import org.example.game.map.GameMap;
import org.example.game.map.MapEditor;
import org.example.game.map.MapManager;
import org.example.game.map.Road;
import org.junit.jupiter.api.*;
import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MapEditorTest {
    private static final String TEST_MAP_NAME = "test_map.map";
    private static MapEditor mapEditor;
    private static GameMap gameMap;

    @BeforeAll
    static void setUp() {
        mapEditor = new MapEditor();
        gameMap = new GameMap(10, 10);
        // Создаем директорию для карт, если её нет
        new File("maps").mkdirs();
    }

    @AfterAll
    static void tearDown() {
        File testMap = new File("maps/" + TEST_MAP_NAME);
        if (testMap.exists()) {
            testMap.delete();
        }
    }

    // === Тесты создания карты ===

    @Nested
    @DisplayName("Тесты создания карты")
    class MapCreationTests {
        @Test
        @DisplayName("Создание карты с валидными размерами")
        void testCreateMapValidSize() {
            GameMap map = new GameMap(10, 10);
            assertNotNull(map);
            assertEquals(10, map.getWidth());
            assertEquals(10, map.getHeight());
        }

        @Test
        @DisplayName("Создание карты с невалидными размерами")
        void testCreateMapInvalidSize() {
            assertThrows(IllegalArgumentException.class, () -> new GameMap(-1, 10));
            assertThrows(IllegalArgumentException.class, () -> new GameMap(10, -1));
            assertThrows(IllegalArgumentException.class, () -> new GameMap(0, 0));
        }

        @Test
        @DisplayName("Создание карты с максимальными размерами")
        void testCreateMapMaxSize() {
            GameMap map = new GameMap(100, 100);
            assertNotNull(map);
            assertEquals(100, map.getWidth());
            assertEquals(100, map.getHeight());
        }
    }

    // === Тесты валидации координат ===

    @Nested
    @DisplayName("Тесты валидации координат")
    class CoordinateValidationTests {
        @Test
        @DisplayName("Проверка валидных координат внутри карты")
        void testValidCoordinatesInside() {
            assertTrue(gameMap.isValidPosition(0, 0));
            assertTrue(gameMap.isValidPosition(5, 5));
            assertTrue(gameMap.isValidPosition(9, 9));
        }

        @Test
        @DisplayName("Проверка невалидных координат за пределами карты")
        void testInvalidCoordinatesOutside() {
            assertFalse(gameMap.isValidPosition(-1, 5));
            assertFalse(gameMap.isValidPosition(5, -1));
            assertFalse(gameMap.isValidPosition(10, 5));
            assertFalse(gameMap.isValidPosition(5, 10));
        }

        @Test
        @DisplayName("Проверка граничных значений координат")
        void testBoundaryCoordinates() {
            assertTrue(gameMap.isValidPosition(0, 0));
            assertTrue(gameMap.isValidPosition(9, 9));
            assertFalse(gameMap.isValidPosition(10, 10));
        }
    }

    // === Тесты размещения объектов ===

    @Nested
    @DisplayName("Тесты размещения объектов")
    class ObjectPlacementTests {
        @Test
        @DisplayName("Размещение всех типов объектов")
        void testPlaceAllObjectTypes() {
            gameMap.setCell(1, 1, 'C'); // Замок
            assertEquals('C', gameMap.getCell(1, 1));
            
            gameMap.setCell(2, 2, 'R'); // Дорога
            assertEquals('R', gameMap.getCell(2, 2));
            
            gameMap.setCell(3, 3, '#'); // Препятствие
            assertEquals('#', gameMap.getCell(3, 3));
            
            gameMap.setCell(4, 4, 'P'); // Равнина
            assertEquals('P', gameMap.getCell(4, 4));
            
            gameMap.setCell(5, 5, 'F'); // Лес
            assertEquals('F', gameMap.getCell(5, 5));
            
            gameMap.setCell(6, 6, 'M'); // Горы
            assertEquals('M', gameMap.getCell(6, 6));
            
            gameMap.setCell(7, 7, 'W'); // Вода
            assertEquals('W', gameMap.getCell(7, 7));
        }

        @Test
        @DisplayName("Размещение объекта на занятую клетку")
        void testPlaceObjectOnOccupiedCell() {
            gameMap.setCell(1, 1, 'C');
            gameMap.setCell(1, 1, 'R');
            assertEquals('R', gameMap.getCell(1, 1));
        }

        @Test
        @DisplayName("Размещение объекта за пределами карты")
        void testPlaceObjectOutOfBounds() {
            assertThrows(IndexOutOfBoundsException.class, () -> gameMap.setCell(-1, 5, 'C'));
            assertThrows(IndexOutOfBoundsException.class, () -> gameMap.setCell(5, -1, 'C'));
            assertThrows(IndexOutOfBoundsException.class, () -> gameMap.setCell(10, 5, 'C'));
            assertThrows(IndexOutOfBoundsException.class, () -> gameMap.setCell(5, 10, 'C'));
        }
    }

    // === Тесты валидации типов местности ===

    @Nested
    @DisplayName("Тесты валидации типов местности")
    class TerrainValidationTests {
        @Test
        @DisplayName("Проверка валидных типов местности")
        void testValidTerrainTypes() {
            assertTrue(gameMap.isValidTerrain('C')); // Замок
            assertTrue(gameMap.isValidTerrain('R')); // Дорога
            assertTrue(gameMap.isValidTerrain('#')); // Препятствие
            assertTrue(gameMap.isValidTerrain('P')); // Равнина
            assertTrue(gameMap.isValidTerrain('F')); // Лес
            assertTrue(gameMap.isValidTerrain('M')); // Горы
            assertTrue(gameMap.isValidTerrain('W')); // Вода
        }

        @Test
        @DisplayName("Проверка невалидных типов местности")
        void testInvalidTerrainTypes() {
            assertFalse(gameMap.isValidTerrain('X'));
            assertFalse(gameMap.isValidTerrain('Y'));
            assertFalse(gameMap.isValidTerrain('Z'));
            assertFalse(gameMap.isValidTerrain(' '));
            assertFalse(gameMap.isValidTerrain('0'));
        }
    }

    // === Тесты сохранения и загрузки ===

    @Nested
    @DisplayName("Тесты сохранения и загрузки")
    class SaveLoadTests {
        @Test
        @DisplayName("Сохранение и загрузка пустой карты")
        void testSaveLoadEmptyMap() {
            MapManager.saveMap(gameMap, "maps" + TEST_MAP_NAME);

            GameMap loadedMap = MapManager.loadMap("maps" + TEST_MAP_NAME);
            assertNotNull(loadedMap);
            assertEquals(gameMap.getWidth(), loadedMap.getWidth());
            assertEquals(gameMap.getHeight(), loadedMap.getHeight());
        }

        @Test
        @DisplayName("Сохранение и загрузка карты с объектами")
        void testSaveLoadMapWithObjects() {
            gameMap.setCell(1, 1, 'C');
            gameMap.setCell(2, 2, 'R');
            gameMap.setCell(3, 3, '#');
            
            MapManager.saveMap(gameMap, "maps" + TEST_MAP_NAME);
            GameMap loadedMap = MapManager.loadMap("maps" + TEST_MAP_NAME);
            
            assertEquals('C', loadedMap.getCell(1, 1));
            assertEquals('R', loadedMap.getCell(2, 2));
            assertEquals('#', loadedMap.getCell(3, 3));
        }

        @Test
        @DisplayName("Загрузка несуществующей карты")
        void testLoadNonexistentMap() {
            assertThrows(RuntimeException.class, () -> 
                MapManager.loadMap("nonexistent_map.map"),
                "Загрузка несуществующей карты должна вызывать RuntimeException");
        }
    }

    // === Тесты проверки размеров и содержимого ===

    @Nested
    @DisplayName("Тесты проверки размеров и содержимого")
    class SizeContentTests {
        @Test
        @DisplayName("Проверка размеров карты")
        void testMapDimensions() {
            assertEquals(10, gameMap.getWidth());
            assertEquals(10, gameMap.getHeight());
            assertEquals(100, gameMap.getWidth() * gameMap.getHeight());
        }

        @Test
        @DisplayName("Проверка начального состояния карты")
        void testInitialMapState() {
            for (int y = 0; y < gameMap.getHeight(); y++) {
                for (int x = 0; x < gameMap.getWidth(); x++) {
                    assertEquals(' ', gameMap.getCell(x, y));
                }
            }
        }

        @Test
        @DisplayName("Проверка очистки карты")
        void testMapClear() {
            gameMap.setCell(1, 1, 'C');
            gameMap.setCell(2, 2, 'R');
            
            gameMap.clear();
            
            for (int y = 0; y < gameMap.getHeight(); y++) {
                for (int x = 0; x < gameMap.getWidth(); x++) {
                    assertEquals(' ', gameMap.getCell(x, y));
                }
            }
        }
    }

    // === Тесты размещения дороги ===

    @Nested
    @DisplayName("Тесты размещения дороги")
    class RoadPlacementTests {
        @Test
        @DisplayName("Размещение горизонтальной дороги")
        void testPlaceHorizontalRoad() {
            Road road = new Road(1, 1, 5, 1);
            road.placeRoad(gameMap.getMap());
            
            for (int x = 1; x <= 5; x++) {
                assertEquals('.', gameMap.getCell(x, 1));
            }
        }

        @Test
        @DisplayName("Размещение вертикальной дороги")
        void testPlaceVerticalRoad() {
            Road road = new Road(1, 1, 1, 5);
            road.placeRoad(gameMap.getMap());
            
            for (int y = 1; y <= 5; y++) {
                assertEquals('.', gameMap.getCell(1, y));
            }
        }

        @Test
        @DisplayName("Размещение диагональной дороги")
        void testPlaceDiagonalRoad() {
            Road road = new Road(1, 1, 5, 5);
            road.placeRoad(gameMap.getMap());
            
            // Проверяем горизонтальную часть
            for (int x = 1; x <= 5; x++) {
                assertEquals('.', gameMap.getCell(x, 1));
            }
            // Проверяем вертикальную часть
            for (int y = 1; y <= 5; y++) {
                assertEquals('.', gameMap.getCell(5, y));
            }
        }
    }
} 