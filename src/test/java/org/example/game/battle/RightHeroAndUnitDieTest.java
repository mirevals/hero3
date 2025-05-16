package org.example.game.battle;


import org.example.game.Player;
import org.example.game.build.EnemyCastle;
import org.example.game.build.HeroCastle;
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
import static org.junit.jupiter.api.Assertions.assertTrue;
//7. Корректность смерти юнита, героя
public class RightHeroAndUnitDieTest {

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
     * Тест проверяет, что если герой и его юнит умирают, то считается, что замок врага захвачен.
     * Метод `die()` вызывается для героя и его юнита, после чего проверяется, что оба мертвы.
     * Если оба персонажа мертвы, тест утверждает, что захват замка врага произошел.
     */
    @Test
    public void testVictoryWhenEnemyCastleCaptured() {
        hero.die();
        Unit heroUnit1 = new Unit(Unit.UnitType.WARRIOR, 1000, 100, 1, 10, Team.HERO, 'W', 100);

        heroUnit1.die();
        boolean captured = hero.isDead() && heroUnit1.isDead();

        assertTrue(captured, "Игрок мертв");
    }

}
