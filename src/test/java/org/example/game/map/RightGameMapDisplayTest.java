package org.example.game.map;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
//10. Корректность отображения поля сражения
public class RightGameMapDisplayTest {
    /**
     * Тест проверяет корректность отображения карты сражения.
     * Сначала создается карта размером 10x10, и на ней размещаются различные символы, представляющие:
     * - Героя (символ 'H') на позиции (0, 0),
     * - Врага (символ 'E') на позиции (1, 1),
     * - Замок героя (символ 'C') на позиции (9, 9),
     * - Замок врага (символ 'C') на позиции (0, 9).
     * После размещения символов проверяется, что символы отображаются в соответствующих клетках карты.
     * Также проверяется, что в других клетках карты отображается символ '.', что обозначает пустую клетку.
     */
    @Test
    public void testBattlefieldDisplayCorrectness() {
        // Настройка окружения
        GameMap gameMap = new GameMap(10, 10); // Карта размером 10x10

        // Заполнение карты символами через setCellValue
        for (int i = 0; i < gameMap.getWidth(); i++) {
            for (int j = 0; j < gameMap.getHeight(); j++) {
                gameMap.setCellValue(i, j, '.'); // Все клетки карты изначально пустые
            }
        }

        // Размещение символов на правильных позициях с использованием setCellValue
        gameMap.setCellValue(0, 0, 'H'); // Герой на (0, 0)
        gameMap.setCellValue(1, 1, 'E'); // Враг на (1, 1)
        gameMap.setCellValue(9, 9, 'C'); // Замок героя на (9, 9)
        gameMap.setCellValue(0, 9, 'C'); // Замок врага на (0, 9)

        // Вывод карты для визуальной проверки
        System.out.println("Map after placement:");
        for (int i = 0; i < gameMap.getHeight(); i++) {
            for (int j = 0; j < gameMap.getWidth(); j++) {
                System.out.print(gameMap.getMap()[i][j] + " ");
            }
            System.out.println();
        }

        // Получаем карту для проверки
        char[][] map = gameMap.getMap();

        // Проверка того, что символы на карте правильные
        assertEquals('H', map[0][0]); // Герой на (0, 0)
        assertEquals('E', map[1][1]); // Враг на (1, 1)
        assertEquals('C', map[9][9]); // Замок героя на (9, 9)
        assertEquals('C', map[9][0]); // Замок врага на (0, 9)

        // Проверка, что на других позициях карты нет символов
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                if ((i == 0 && j == 0) || (i == 1 && j == 1) || (i == 9 && j == 9) || (i == 0 && j == 9)) {
                    continue; // Пропускаем уже проверенные позиции
                }
                assertEquals('.', map[j][i]); // Проверяем, что на других позициях нет объектов
            }
        }

        // Тест прошел, если все проверки прошли успешно.
    }
}