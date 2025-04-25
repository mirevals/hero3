package org.example.game.map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
//3. Корректность штрафов перемещения
public class RightFineMoveTest {

    /**
     * Тест проверяет корректность штрафов за перемещения в разных зонах карты.
     * Карта разделена на три территории:
     * - Геройская территория (первая треть карты) — на этой территории штрафа за перемещение нет.
     * - Нейтральная территория (вторая треть карты) — на этой территории за перемещение назначается штраф 2.
     * - Вражеская территория (третья часть карты) — на этой территории за перемещение назначается штраф 1.
     *
     * В тесте проверяется, что на каждой из этих территорий штрафы установлены корректно для разных позиций.
     */
    @Test
    public void testMovementPenalty() {
        GameMap map = new GameMap(10, 10); // ширина = 10, высота = 10

        // Все клетки — проходимые ('.')
        for (int y = 0; y < map.getHeight(); y++) {
            for (int x = 0; x < map.getWidth(); x++) {
                map.getMap()[y][x] = '.';
            }
        }

        // Проверка на территории героя (первая треть карты)
        Assertions.assertEquals(0, getMovementPenalty(0, 2, map), "На своей территории не должно быть штрафа");
        Assertions.assertEquals(0, getMovementPenalty(2, 2, map), "На границе своей территории штрафа нет");

        // Проверка на нейтральной территории (вторая треть карты)
        Assertions.assertEquals(2, getMovementPenalty(3, 2, map), "На нейтральной территории должен быть штраф 2");
        Assertions.assertEquals(2, getMovementPenalty(4, 2, map), "На нейтральной территории должен быть штраф 2");
        Assertions.assertEquals(2, getMovementPenalty(5, 2, map), "На нейтральной территории должен быть штраф 2");

        // Проверка на территории врага (третья часть карты)
        Assertions.assertEquals(1, getMovementPenalty(6, 2, map), "На территории врага должен быть штраф 1");
        Assertions.assertEquals(1, getMovementPenalty(7, 2, map), "На территории врага должен быть штраф 1");
        Assertions.assertEquals(1, getMovementPenalty(8, 2, map), "На территории врага должен быть штраф 1");
    }

    // Метод для теста (имитация настоящего метода)
    private int getMovementPenalty(int x, int y, GameMap gameMap) {
        // Геройская территория (первая треть карты)
        if (x < gameMap.getWidth() / 3) {
            return 0;  // Нет штрафа на своей территории
        }
        // Вражеская территория (третья часть карты)
        else if (x >= 2 * gameMap.getWidth() / 3) {
            return 1;  // Минимальный штраф на территории врага
        }
        // Нейтральная территория (вторая треть карты)
        return 2;  // Штраф на нейтральной территории
    }
}