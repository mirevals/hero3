package org.example.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameMapTest {
    private GameMap gameMap;

    @BeforeEach
    public void setUp() {
        gameMap = new GameMap(9, 5);  // Пример карты 9x5
    }


    @Test
    public void testHeroPlacement() {
        // Проверка, что герой размещен в области игрока
        char heroPosition = gameMap.getMap()[gameMap.getHeroY()][gameMap.getHeroX()];
        assertEquals('H', heroPosition, "Герой должен быть размещен в области игрока.");
    }

    @Test
    public void testMoveHeroValid() {
        // Инициализация героя
        Position startPosition = new Position(gameMap.getHeroX(), gameMap.getHeroY()); // Начальная позиция героя на карте
        Team team = Team.PLAYER;  // Создаем команду героя
        Unit warriorUnit = new Unit(Unit.UnitType.WARRIOR, 100, 10, 5, 3, team, startPosition);

        Hero hero = new Hero("Hero", 2, startPosition, warriorUnit, team);  // Герой с 2 перемещениями за ход

        // Переместим героя вправо
        boolean moveSuccessful = gameMap.moveHero(1, 0, hero);
        assertTrue(moveSuccessful, "Перемещение героя вправо должно быть успешным.");

        // Проверим новую позицию героя
        assertEquals('H', gameMap.getMap()[gameMap.getHeroY()][gameMap.getHeroX()],
                "Герой должен быть перемещен в новую позицию.");
    }

    @Test
    public void testMoveHeroInvalidOutOfBounds() {
        // Инициализация героя
        Position startPosition = new Position(gameMap.getHeroX(), gameMap.getHeroY()); // Начальная позиция героя на карте
        Team team = Team.PLAYER;  // Создаем команду героя
        Unit warriorUnit = new Unit(Unit.UnitType.WARRIOR, 100, 10, 5, 3, team, startPosition);

        Hero hero = new Hero("Hero", 2, startPosition, warriorUnit, team);  // Герой с 2 перемещениями за ход

        // Переместим героя за пределы карты
        gameMap.moveHero(1, 0, hero); // Перемещаем на одну клетку вправо
        boolean moveOutOfBounds = gameMap.moveHero(10, 0, hero);  // Попытка перемещения за пределы карты
        assertFalse(moveOutOfBounds, "Перемещение за пределы карты должно быть запрещено.");
    }


    @Test
    public void testHeroPositionAfterMove() {
        // Инициализация героя
        Position startPosition = new Position(gameMap.getHeroX(), gameMap.getHeroY()); // Начальная позиция героя на карте
        Team team = Team.PLAYER;  // Создаем команду героя
        Unit warriorUnit = new Unit(Unit.UnitType.WARRIOR, 100, 10, 5, 3, team, startPosition);

        Hero hero = new Hero("Hero", 2, startPosition, warriorUnit, team);  // Герой с 2 перемещениями за ход

        // Проверим, что герой правильно перемещается
        int initialX = gameMap.getHeroX();
        int initialY = gameMap.getHeroY();
        gameMap.moveHero(1, 0, hero);  // Перемещаем вправо
        assertNotEquals(initialX, gameMap.getHeroX(), "Координата X героя должна измениться.");
        assertEquals(initialY, gameMap.getHeroY(), "Координата Y героя не должна измениться.");
    }


}