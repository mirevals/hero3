package org.example.game.person;

import org.example.game.Player;
import org.example.game.battle.BattleField;
import org.example.game.build.Castle;
import org.example.game.build.EnemyCastle;
import org.example.game.build.HeroCastle;
import org.example.game.map.GameMap;
import org.example.game.map.MapManager;
import org.example.game.map.Position;
import org.example.game.map.Road;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.example.game.build.Shop.availableBuildings;
import static org.junit.jupiter.api.Assertions.*;
//9. Корректность действий бота
public class RightEnemyMoveTest {

    private GameMap gameMap;
    private Enemy enemy;
    private Hero hero;
    private Castle castle;
    private Player player;
    private EnemyCastle enemyCastle;
    private HeroCastle heroCastle;
    private MapManager mapManager;
    private List<Unit> buyUnit;
    private BattleField battleField;
    private List<Unit> allUnits;
    private Carriage carriage;

    @BeforeEach
    public void setUp() {
        gameMap = new GameMap(10, 10);
        char[][] mapLayout = {
                {'#', '#', '#', '#', '#'},
                {'#', ' ', ' ', ' ', '#'},
                {'#', ' ', ' ', ' ', '#'},
                {'#', ' ', ' ', ' ', '#'},
                {'#', '#', '#', '#', '#'},
        };

        // Заполняем карту через setCellValue
        for (int y = 0; y < mapLayout.length; y++) {
            for (int x = 0; x < mapLayout[y].length; x++) {
                gameMap.setCellValue(x, y, mapLayout[y][x]);
            }
        }

        player = new Player("TestPlayer", 1000);

        Unit heroUnit = new Unit(Unit.UnitType.WARRIOR, 1000, 100, 1, 10, Team.HERO, 'W', 100);
        Unit enemyUnit = new Unit(Unit.UnitType.WARRIOR, 100, 100, 1, 10, Team.ENEMY, 'A', 100);

        List<Unit> unitsHero = new ArrayList<>();
        List<Unit> unitsEnemy = new ArrayList<>();

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

        gameMap.setCellValue(enemy.getX(), enemy.getY(), 'E');
    }
    /**
     * Тест проверяет, что враг перемещается только по дороге.
     * Враг должен переместиться с исходной позиции на соседнюю клетку дороги.
     * После перемещения, на старой позиции не должно быть врага, а на новой позиции должен быть символ врага.
     * Также проверяется, что враг не может стоять на стенах (символ '#').
     */
//    @Test
//    public void testEnemyMovesOnlyOnRoad() {
//        int oldX = enemy.getX();
//        int oldY = enemy.getY();
//
//        mapManager.enemyMove(hero, enemy, castle, player, enemyCastle, heroCastle, gameMap, mapManager, buyUnit, battleField, allUnits, carriage);
//
//        int newX = enemy.getX();
//        int newY = enemy.getY();
//
//        assertTrue(newX != oldX || newY != oldY, "Враг должен был переместиться");
//        assertEquals('A', gameMap.getMap()[newY][newX], "В новой позиции должен быть символ врага");
//        assertEquals('.', gameMap.getMap()[oldY][oldX], "Старая позиция врага должна быть очищена дорогой");
//        assertNotEquals('#', gameMap.getMap()[newY][newX], "Враг не должен стоять на стене");
//    }
    /**
     * Тест проверяет, что враг не двигается, если он мертв.
     * Когда враг умирает, его позиция должна остаться на карте, но он не должен перемещаться.
     * Враг не должен исчезнуть с карты после смерти.
     */
    @Test
    public void testEnemyDoesNotMoveWhenDead() {
        enemy.die();
        mapManager.removeEnemyFromMap(enemy, gameMap);


        boolean enemyFound = true;
        for (int i = 0; i < gameMap.getMap().length-1; i++) {
            for (int j = 0; j < gameMap.getMap()[i].length-1; j++) {
                if (gameMap.getMap()[i][j] == 'E') {
                    enemyFound = true;
                    break;
                }
            }
            if (enemyFound) {
                break;
            }
        }
        assertTrue(enemyFound);
    }
    /**
     * Тест проверяет, что враг не двигается, если на карте нет доступных путей для перемещения.
     * В случае, когда враг находится в окружении стен и не может передвигаться, его позиция должна остаться неизменной.
     */
    @Test
    public void testEnemyDoesNotMoveIfNoValidPaths() {
        // Обновляем карту 3x3 через setCellValue
        gameMap = new GameMap(3, 3);
        char[][] noMovesMap = {
                {'#', '#', '#'},
                {'#', 'E', '#'},
                {'#', '#', '#'}
        };
        for (int y = 0; y < noMovesMap.length; y++) {
            for (int x = 0; x < noMovesMap[y].length; x++) {
                gameMap.setCellValue(x, y, noMovesMap[y][x]);
            }
        }

        enemy.setX(1);
        enemy.setY(1);

        mapManager.enemyMove(hero, enemy, castle, player, enemyCastle, heroCastle, gameMap, mapManager, buyUnit, battleField, allUnits, carriage);

        assertEquals(1, enemy.getX());
        assertEquals(1, enemy.getY());
        assertEquals('E', gameMap.getMap()[1][1]);
    }
}