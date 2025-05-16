package org.example.game.map;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MapModificationTest {
    private static final int MAP_WIDTH = 10;
    private static final int MAP_HEIGHT = 10;
    private GameMap testMap;

    @BeforeEach
    void setUp() {
        testMap = new GameMap(MAP_WIDTH, MAP_HEIGHT);
        // Инициализация карты с некоторыми объектами
        testMap.setCellValue(1, 1, 'C'); // Замок
        testMap.setCellValue(2, 2, 'R'); // Дорога
        testMap.setCellValue(3, 3, '#'); // Препятствие
    }

    @Test
    @Order(1)
    @DisplayName("Изменение типа местности на существующей клетке")
    void testModifyExistingCell() {
        // Изменяем тип местности на существующей клетке
        testMap.setCellValue(1, 1, 'P');
        assertEquals('P', testMap.getCellValue(1, 1), "Клетка должна быть изменена на равнину");

        testMap.setCellValue(2, 2, 'F');
        assertEquals('F', testMap.getCellValue(2, 2), "Клетка должна быть изменена на лес");
    }

    @Test
    @Order(2)
    @DisplayName("Последовательное изменение одной клетки")
    void testSequentialCellModification() {
        // Последовательно меняем тип местности на одной клетке
        testMap.setCellValue(1, 1, 'P'); // Равнина
        assertEquals('P', testMap.getCellValue(1, 1));
        
        testMap.setCellValue(1, 1, 'F'); // Лес
        assertEquals('F', testMap.getCellValue(1, 1));
        
        testMap.setCellValue(1, 1, 'M'); // Горы
        assertEquals('M', testMap.getCellValue(1, 1));
        
        testMap.setCellValue(1, 1, 'W'); // Вода
        assertEquals('W', testMap.getCellValue(1, 1));
    }

    @Test
    @Order(3)
    @DisplayName("Изменение группы клеток")
    void testModifyMultipleCells() {
        // Изменяем группу клеток и проверяем их состояние
        char[][] pattern = {
            {'P', 'F', 'M'},
            {'F', 'W', 'P'},
            {'M', 'P', 'F'}
        };

        // Размещаем паттерн на карте
        for (int y = 0; y < pattern.length; y++) {
            for (int x = 0; x < pattern[y].length; x++) {
                testMap.setCellValue(x, y, pattern[y][x]);
            }
        }

        // Проверяем, что все клетки установлены правильно
        for (int y = 0; y < pattern.length; y++) {
            for (int x = 0; x < pattern[y].length; x++) {
                assertEquals(pattern[y][x], testMap.getCellValue(x, y),
                        String.format("Клетка (%d,%d) должна содержать '%c'", x, y, pattern[y][x]));
            }
        }
    }

    @Test
    @Order(4)
    @DisplayName("Изменение клеток на границе карты")
    void testModifyBorderCells() {
        // Изменяем клетки на границах карты
        testMap.setCellValue(0, 0, 'P');
        assertEquals('P', testMap.getCellValue(0, 0), "Верхний левый угол");

        testMap.setCellValue(MAP_WIDTH - 1, 0, 'F');
        assertEquals('F', testMap.getCellValue(MAP_WIDTH - 1, 0), "Верхний правый угол");

        testMap.setCellValue(0, MAP_HEIGHT - 1, 'M');
        assertEquals('M', testMap.getCellValue(0, MAP_HEIGHT - 1), "Нижний левый угол");

        testMap.setCellValue(MAP_WIDTH - 1, MAP_HEIGHT - 1, 'W');
        assertEquals('W', testMap.getCellValue(MAP_WIDTH - 1, MAP_HEIGHT - 1), "Нижний правый угол");
    }

    @Test
    @Order(5)
    @DisplayName("Проверка сохранения окружающих клеток при изменении")
    void testSurroundingCellsPreservation() {
        // Создаем паттерн из клеток
        testMap.setCellValue(4, 4, 'C');
        testMap.setCellValue(3, 4, 'P');
        testMap.setCellValue(5, 4, 'F');
        testMap.setCellValue(4, 3, 'M');
        testMap.setCellValue(4, 5, 'W');

        // Изменяем центральную клетку
        testMap.setCellValue(4, 4, 'R');

        // Проверяем, что окружающие клетки не изменились
        assertEquals('P', testMap.getCellValue(3, 4), "Клетка слева должна остаться неизменной");
        assertEquals('F', testMap.getCellValue(5, 4), "Клетка справа должна остаться неизменной");
        assertEquals('M', testMap.getCellValue(4, 3), "Клетка сверху должна остаться неизменной");
        assertEquals('W', testMap.getCellValue(4, 5), "Клетка снизу должна остаться неизменной");
        assertEquals('R', testMap.getCellValue(4, 4), "Центральная клетка должна быть изменена");
    }

    @Test
    @Order(6)
    @DisplayName("Проверка изменения дороги")
    void testRoadModification() {
        // Создаем дорогу
        Road road = new Road(1, 1, 5, 1);
        road.placeRoad(testMap.getMap());

        // Изменяем часть дороги на другой тип местности
        testMap.setCellValue(3, 1, 'P');

        // Проверяем, что часть дороги изменилась, а остальные участки остались
        assertEquals('.', testMap.getCellValue(1, 1), "Начало дороги должно остаться неизменным");
        assertEquals('.', testMap.getCellValue(2, 1), "Второй участок дороги должен остаться неизменным");
        assertEquals('P', testMap.getCellValue(3, 1), "Измененный участок должен стать равниной");
        assertEquals('.', testMap.getCellValue(4, 1), "Четвертый участок дороги должен остаться неизменным");
        assertEquals('.', testMap.getCellValue(5, 1), "Конец дороги должен остаться неизменным");
    }

    @Test
    @Order(7)
    @DisplayName("Проверка изменения замка")
    void testCastleModification() {
        // Размещаем замок
        testMap.setCellValue(5, 5, 'C');
        assertEquals('C', testMap.getCellValue(5, 5), "Замок должен быть размещен");

        // Пытаемся изменить замок на другой тип местности
        testMap.setCellValue(5, 5, 'P');
        assertEquals('P', testMap.getCellValue(5, 5), "Замок должен быть заменен на равнину");
    }

    @Test
    @Order(8)
    @DisplayName("Проверка изменения препятствий")
    void testObstacleModification() {
        // Размещаем препятствия в форме квадрата
        for (int y = 2; y <= 4; y++) {
            for (int x = 2; x <= 4; x++) {
                testMap.setCellValue(x, y, '#');
            }
        }

        // Изменяем часть препятствий на проходимую местность
        testMap.setCellValue(3, 3, 'P');
        testMap.setCellValue(3, 2, 'F');
        testMap.setCellValue(2, 3, 'M');

        // Проверяем изменения
        assertEquals('P', testMap.getCellValue(3, 3), "Центральное препятствие должно стать равниной");
        assertEquals('F', testMap.getCellValue(3, 2), "Верхнее препятствие должно стать лесом");
        assertEquals('M', testMap.getCellValue(2, 3), "Левое препятствие должно стать горами");
        assertEquals('#', testMap.getCellValue(4, 4), "Угловое препятствие должно остаться неизменным");
    }
} 