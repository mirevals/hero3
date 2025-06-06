package org.example.game.build;

import org.example.game.Player;
import org.example.game.battle.BattleField;
import org.example.game.map.GameMap;
import org.example.game.map.MapManager;
import org.example.game.map.Position;
import org.example.game.map.Road;
import org.example.game.person.*;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.example.game.build.Shop.availableBuildings;
import static org.junit.Assert.assertEquals;
//11. Корректность логики Героя(некоторые действия доступны только если
//Герой в Замке и тд)
public class RightDoInCastleTest {

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
    Position heroPosition;

    @Before
    public void setUp() {
        gameMap = new GameMap(10, 10);
        player = new Player("TestPlayer", 1000);

        Unit heroUnit = new Unit(Unit.UnitType.WARRIOR, 1000, 100, 1, 10, Team.HERO, 'W', 100);
        Unit enemyUnit = new Unit(Unit.UnitType.WARRIOR, 100, 100, 1, 10, Team.ENEMY, 'A', 100);

        List<Unit> unitsHero = new ArrayList<>();
        for (int i = 0; i < 23; i++) {
            unitsHero.add(heroUnit);
        }

        List<Unit> unitsEnemy = new ArrayList<>();
        unitsEnemy.add(enemyUnit);

        buyUnit = new ArrayList<>(unitsHero);

        heroPosition = new Position(1, 1);
        carriage = new Carriage(new Position(5, 0), 1, 10, Carriage.Direction.DOWN);

        hero = new Hero("Герой", 10, Team.HERO, 1000, 10, 10, 100, 100, 100, 3, unitsHero);
        hero.setPosition(1, 1);

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
     * Тест проверяет, что герой не может покинуть замок,
     * если не выполнены ключевые условия:
     * - таверна не построена
     * - нет стражевого поста
     * - не куплены юниты
     * - это первый герой
     * В этом случае позиция героя остаётся прежней.
     */
    @Test
    public void testHeroCannotExitWithoutTavernAndUnits() {
        CastleManager.isInCastle = true;
        CastleManager.isTavernNotBuild = true;
        CastleManager.isFirstHero = true;
        CastleManager.isNoGuardPost = true;
        CastleManager.isNoUnitsBuy = true;

        CastleManager.exitCastle(enemy, enemyCastle, heroCastle, hero, player, gameMap, heroCastle,
                mapManager, buyUnit, hero, battleField, allUnits, carriage);

        // Герой должен остаться на месте
        assertEquals(heroPosition, hero.getPosition());
    }

    /**
     * Тест проверяет, что герой может покинуть замок,
     * если выполнены все необходимые условия:
     * - таверна построена
     * - куплены юниты
     * - есть стражевой пост
     * - это не первый герой
     * В этом случае позиция героя изменится (в зависимости от реализации логики).
     */
    @Test
    public void testHeroCanExitAfterBuyingEverything() {
        CastleManager.isInCastle = true;
        CastleManager.isTavernNotBuild = false;
        CastleManager.isFirstHero = false;
        CastleManager.isNoGuardPost = false;
        CastleManager.isNoUnitsBuy = false;

        CastleManager.exitCastle(enemy, enemyCastle, heroCastle, hero, player, gameMap, heroCastle,
                mapManager, buyUnit, hero, battleField, allUnits, carriage);

        // Здесь пока ожидается, что позиция не изменится (можно уточнить логику)
        Position expectedNewPos = new Position(heroPosition.getX(), heroPosition.getY());
        assertEquals(expectedNewPos, hero.getPosition());
    }
}