package org.example.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GameMapTest {
    private GameMap gameMap;
    private ByteArrayOutputStream outputStream;



    @BeforeEach
    public void setUp() {
        gameMap = new GameMap(9, 5);  // Пример карты 9x5
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }



    @Test
    public void testMoveHeroInvalidOutOfBounds() {
        // Инициализация карты и героя
        GameMap gameMap = new GameMap(10, 10); // Инициализация карты с размерами 10x10
        Position startPosition = new Position(gameMap.getHeroX(), gameMap.getHeroY()); // Начальная позиция героя на карте
        Team team = Team.HERO;  // Создаем команду героя

        // Создание списка юнитов для героя
        List<Unit> heroUnits = new ArrayList<>();
        heroUnits.add(new Unit(Unit.UnitType.WARRIOR, 100, 10, 5, 3, team, startPosition));

        // Создание героя с переданным списком юнитов
        Character character = new Character("Hero", 2, startPosition, heroUnits, team);  // Герой с 2 перемещениями за ход

        // Создание врага (аналогично герою)
        List<Unit> enemyUnits = new ArrayList<>();
        enemyUnits.add(new Unit(Unit.UnitType.WARRIOR, 100, 10, 5, 3, Team.ENEMY, new Position(gameMap.getEnemyX(), gameMap.getEnemyY())));
        Enemy enemy = new Enemy("Enemy", 2, new Position(gameMap.getEnemyX(), gameMap.getEnemyY()), enemyUnits, Team.ENEMY);

        // Переместим героя на одну клетку вправо
        gameMap.moveHero(1, 0, character, enemy);

        // Попытка перемещения за пределы карты
        boolean moveOutOfBounds = gameMap.moveHero(10, 0, character, enemy);  // Попытка перемещения за пределы карты
        assertFalse(moveOutOfBounds, "Перемещение за пределы карты должно быть запрещено.");
    }


    @Test
    public void testHeroPositionAfterMove() {
        // Инициализация карты и героя
        GameMap gameMap = new GameMap(10, 10);
        Position startPosition = new Position(gameMap.getHeroX(), gameMap.getHeroY());
        Team team = Team.HERO;

        // Создание списка юнитов для героя
        List<Unit> heroUnits = new ArrayList<>();
        heroUnits.add(new Unit(Unit.UnitType.WARRIOR, 100, 10, 5, 3, team, startPosition));

        // Создание героя с переданным списком юнитов
        Character character = new Character("Hero", 2, startPosition, heroUnits, team);

        // Создание врага (аналогично герою)
        List<Unit> enemyUnits = new ArrayList<>();
        enemyUnits.add(new Unit(Unit.UnitType.WARRIOR, 100, 10, 5, 3, Team.ENEMY, new Position(gameMap.getEnemyX(), gameMap.getEnemyY())));
        Enemy enemy = new Enemy("Enemy", 2, new Position(gameMap.getEnemyX(), gameMap.getEnemyY()), enemyUnits, Team.ENEMY);

        // Сохранение начальной позиции
        int initialX = character.getPosition().getX();
        int initialY = character.getPosition().getY();

        // Перемещаем героя вправо
        boolean moved = gameMap.moveHero(1, 0, character, enemy);

        // Проверки
        assertTrue(moved, "Герой должен успешно переместиться.");
        assertEquals(initialX + 1, character.getPosition().getX(), "Координата X героя должна увеличиться.");
        assertEquals(initialY, character.getPosition().getY(), "Координата Y героя не должна измениться.");
        assertEquals(1, character.getCurrentMoves(), "Число оставшихся перемещений должно уменьшиться.");
    }




}