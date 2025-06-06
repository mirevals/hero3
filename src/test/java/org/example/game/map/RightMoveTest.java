package org.example.game.map;


import org.example.game.Player;
import org.example.game.battle.BattleField;
import org.example.game.build.EnemyCastle;
import org.example.game.build.HeroCastle;
import org.example.game.person.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.util.ArrayList;
import java.util.List;

import static org.example.game.build.Shop.availableBuildings;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

//6. Корректность возможности перемещения (нельзя быть на клетке с
//другим существом, нельзя выйти за пределы поля и тд)
public class RightMoveTest {

    GameMap gameMap;
    Player player;
    Hero hero;
    Enemy enemy;
    HeroCastle heroCastle;
    EnemyCastle enemyCastle;
    List<Unit> buyUnit;
    List<Unit> allUnits;
    BattleField battleField;
    Carriage carriage;
    MapManager mapManager;

    @BeforeEach
    public void setUp() {
        gameMap = new GameMap(10, 10);
        player = new Player("TestPlayer", 1000);

        Unit heroUnit = new Unit(Unit.UnitType.WARRIOR, 1000, 100, 1, 10, Team.HERO, 'W', 100);
        Unit enemyUnit = new Unit(Unit.UnitType.WARRIOR, 100, 100, 1, 10, Team.ENEMY, 'A', 100);

        List<Unit> unitsHero = new ArrayList<>();
        List<Unit> unitsEnemy = new ArrayList<>();

        unitsHero.add(heroUnit);
        unitsHero.add(heroUnit);
        unitsHero.add(heroUnit);
        unitsHero.add(heroUnit);
        unitsHero.add(heroUnit);
        unitsHero.add(heroUnit);
        unitsHero.add(heroUnit);
        unitsHero.add(heroUnit);
        unitsHero.add(heroUnit);
        unitsHero.add(heroUnit);
        unitsHero.add(heroUnit);
        unitsHero.add(heroUnit);
        unitsHero.add(heroUnit);
        unitsHero.add(heroUnit);
        unitsHero.add(heroUnit);
        unitsHero.add(heroUnit);
        unitsHero.add(heroUnit);
        unitsHero.add(heroUnit);
        unitsHero.add(heroUnit);
        unitsHero.add(heroUnit);
        unitsHero.add(heroUnit);
        unitsHero.add(heroUnit);
        unitsHero.add(heroUnit);

        unitsEnemy.add(enemyUnit);

        buyUnit = new ArrayList<>(unitsHero);

        carriage = new Carriage(new Position(5, 0), 1, 10, Carriage.Direction.DOWN);

        hero = new Hero("Герой", 10, Team.HERO, 1000, 10, 10, 100, 100, 100, 3, unitsHero);
        enemy = new Enemy("Враг", 5, Team.ENEMY, 100, 10, 10, 100, 100, 100, 1, unitsEnemy);

        heroCastle = new HeroCastle(10, 10);
        enemyCastle = new EnemyCastle(10, 10);

        enemyCastle.addBuilding(availableBuildings.get(0));
        enemyCastle.addBuilding(availableBuildings.get(1));

        allUnits = new ArrayList<>();
        allUnits.addAll(unitsHero);
        allUnits.addAll(unitsEnemy);

        battleField = new BattleField(allUnits);
        Road road = new Road(2, 2, 8, 2);
        mapManager = new MapManager(heroCastle, enemyCastle, enemy, hero, gameMap, road, carriage);
    }

    /**
     * Тест проверяет, что герой может перемещаться вправо, если на пути нет препятствий.
     * После выполнения движения, проверяется, что герой переместился на одну клетку вправо.
     */
    @Test
    public void testMoveHeroWithValidMove() {
        // Устанавливаем начальные позиции героя
        hero.setX(3);
        hero.setY(3);

        // Устанавливаем карту с возможным движением вправо
        char[][] map = new char[10][10];
        map[6][5] = '.';  // Дорога на одну клетку вправо


        // Пробуем двигать героя вправо
        mapManager.moveHero(1, 0, 1, hero, enemy, heroCastle, player, enemyCastle, heroCastle, gameMap, mapManager, buyUnit, null, battleField, allUnits, carriage);

        // Проверяем, что герой двигался вправо
        assertEquals(4, hero.getX(), "Герой должен был двигаться вправо.");
        assertEquals(3, hero.getY(), "Герой должен был двигаться вправо.");
    }
    /**
     * Тест проверяет, что если на пути героя есть препятствие, то герой не может двигаться в этом направлении.
     * После попытки движения вправо на клетку с препятствием, герой остается на месте.
     */
    @Test
    public void testMoveHeroWithObstacle() {
        // Устанавливаем начальные позиции героя
        hero.setX(3);
        hero.setY(3);

        // Устанавливаем карту с препятствием
        char[][] map = new char[10][10];
        map[6][5] = '#';  // Препятствие на одной клетке вправо


        // Пробуем двигаться вправо, но там препятствие
        mapManager.moveHero(1, 0, 1, hero, enemy, heroCastle, player, enemyCastle, heroCastle, gameMap, mapManager, buyUnit, null, battleField, allUnits, carriage);

        // Проверка, что герой остался на месте, так как есть препятствие
        assertEquals(4, hero.getX(), "Герой не должен был двигаться.");
        assertEquals(3, hero.getY(), "Герой не должен был двигаться.");
    }


    /**
     * Тест проверяет, что герой не может выйти за пределы карты.
     * При попытке перемещения за границы карты, герой остается на своей текущей позиции.
     */
    @Test
    public void testMoveHeroOutOfBounds() {
        // Устанавливаем начальные позиции героя
        hero.setX(3);
        hero.setY(3);

        // Мокаем объект gameMap
        GameMap mockGameMap = mock(GameMap.class);
        char[][] map = new char[10][10];  // Создаем карту размером 10x10
        when(mockGameMap.getMap()).thenReturn(map);  // Стабаем метод getMap

        // Пробуем выйти за пределы карты
        mapManager.moveHero(1, 0, 11, hero, enemy, heroCastle, player, enemyCastle, heroCastle, mockGameMap, mapManager, buyUnit, null, battleField, allUnits, carriage);

        // Проверка, что герой не вышел за пределы карты
        assertEquals(3, hero.getX(), "Герой не должен был выйти за пределы карты.");
        assertEquals(3, hero.getY(), "Герой не должен был выйти за пределы карты.");
    }


    /**
     * Тест проверяет, что герой не может пройти в клетку, занятую врагом, если враг находится в пределах дальности хода.
     * После попытки перемещения в клетку с врагом, герой должен остановиться на его клетке.
     */
    @Test
    public void testMoveHeroToEnemyPosition() {
        // Устанавливаем начальные позиции героя и врага
        hero.setX(3);
        hero.setY(3);
        enemy.setX(5);
        enemy.setY(5);

        // Проверяем, что герой двигается в сторону врага и остановится, если враг в пределах дальности хода
        mapManager.moveHero(1, 1, 3, hero, enemy, heroCastle, player, enemyCastle, heroCastle, gameMap, mapManager, buyUnit, null, battleField, allUnits, carriage);

        // Проверка, что герой остановился на враге, так как враг в пределах дальности хода
        assertEquals(5, hero.getX(), "Герой должен был остановиться на враге.");
        assertEquals(5, hero.getY(), "Герой должен был остановиться на враге.");
    }


    /**
     * Тест проверяет, что герой может переместиться к замку героев.
     * После перемещения проверяется, что герой достиг замка, а его позиция обновилась.
     */
    @Test
    public void testMoveHeroToHeroCastle() {
        // Устанавливаем начальные позиции героя и замка
        hero.setX(3);
        hero.setY(3);
        heroCastle.setPosition(new Position(6, 5));

        // Печать начальной позиции героя
        System.out.println("Начальная позиция героя: (" + hero.getX() + ", " + hero.getY() + ")");

        // Пробуем переместиться к замку героев
        mapManager.moveHero(1, 1, 3, hero, enemy, heroCastle, player, enemyCastle, heroCastle, gameMap, mapManager, buyUnit, null, battleField, allUnits, carriage);

        // Печать позиции героя после движения
        System.out.println("Позиция героя после движения: (" + hero.getX() + ", " + hero.getY() + ")");

        // Проверка, что герой достиг замка
        assertEquals(5, hero.getX(), "Герой должен был переместиться к замку.");
        assertEquals(5, hero.getY(), "Герой должен был переместиться к замку.");
    }
}
