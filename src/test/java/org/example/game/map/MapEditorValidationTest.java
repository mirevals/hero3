package org.example.game.map;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MapEditorValidationTest {
    private static final int MAP_WIDTH = 10;
    private static final int MAP_HEIGHT = 10;
    private GameMap gameMap;
    private MapEditor mapEditor;

    @BeforeEach
    void setUp() {
        gameMap = new GameMap(MAP_WIDTH, MAP_HEIGHT);
        mapEditor = new MapEditor();
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


} 