package org.example.game.build;

import org.example.game.Player;
import org.example.game.battle.BattleField;
import org.example.game.map.GameMap;
import org.example.game.map.MapManager;
import org.example.game.map.Position;
import org.example.game.map.Road;
import org.example.game.person.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.example.game.build.Shop.availableBuildings;
import static org.junit.jupiter.api.Assertions.*;
//8. Корректность работы всех зданий
public class RightBuildingTest {

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

    private Building tavern;
    private Building guardPost;

    @BeforeEach
    public void setUp() {
        gameMap = new GameMap(10, 10);
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


        CastleManager.isTavernNotBuild = true;
        CastleManager.isNoGuardPost = true;

        Shop.availableBuildings = new ArrayList<>();
        tavern = new Tavern();
        guardPost = new GuardPost();

        Shop.availableBuildings.add(tavern);
        Shop.availableBuildings.add(guardPost);

    }

    /**
     * Тест проверяет корректность покупки таверны.
     * После покупки таверны, она должна быть добавлена в список построенных зданий замка,
     * флаг `isTavernNotBuild` должен быть обновлен, а результат покупки должен быть равен объекту таверны.
     */
    @Test
    void testBuyTavern() {
        Building result = CastleManager.buyBuilding(1, heroCastle);

        assertNotNull(result);
        assertEquals(tavern, result);
        assertTrue(heroCastle.getConstructedBuildings().contains(tavern));
        assertFalse(CastleManager.isTavernNotBuild);
    }
    /**
     * Тест проверяет корректность покупки поста охраны.
     * После покупки поста охраны, он должен быть добавлен в список построенных зданий замка,
     * флаг `isNoGuardPost` должен быть обновлен, а результат покупки должен быть равен объекту поста охраны.
     */
    @Test
    void testBuyGuardPost() {
        Building result = CastleManager.buyBuilding(2, heroCastle);

        assertNotNull(result);
        assertEquals(guardPost, result);
        assertTrue(heroCastle.getConstructedBuildings().contains(guardPost));
        assertFalse(CastleManager.isNoGuardPost);
    }
    /**
     * Тест проверяет, что покупка недопустимого здания (с идентификатором 999) возвращает null,
     * и список построенных зданий замка остается пустым.
     */
    @Test
    void testBuyInvalidBuilding() {
        Building result = CastleManager.buyBuilding(999, heroCastle);
        assertNull(result);
        assertTrue(heroCastle.getConstructedBuildings().isEmpty());
    }
}
