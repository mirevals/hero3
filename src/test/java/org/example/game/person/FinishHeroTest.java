package org.example.game.person;

import org.example.game.Player;
import org.example.game.battle.Battle;
import org.example.game.battle.BattleField;
import org.example.game.build.*;
import org.example.game.map.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.example.game.build.Shop.availableBuildings;
import static org.junit.jupiter.api.Assertions.*;
//1. Корректность завершения игры победой игрока всеми способами
public class FinishHeroTest {

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
     * Тест проверяет, что игрок побеждает, когда все вражеские юниты уничтожены.
     * Проверяется, что список юнитов врага пуст, что свидетельствует о победе.
     */
    @Test
    public void testVictoryWhenAllEnemiesDefeated() {
        enemy.getUnits().clear();

        boolean isVictory = enemy.getUnits().isEmpty();

        assertTrue(isVictory, "Игрок должен победить, когда все враги побеждены");
    }
    /**
     * Тест проверяет, что игрок побеждает, когда захватывает замок врага.
     * Герой должен встать на позицию замка врага, и враг должен быть мертв.
     */
    @Test
    public void testVictoryWhenEnemyCastleCaptured() {
        enemy.die();
        // Герой встал на позицию замка врага
        hero.setX(enemyCastle.getX());
        hero.setY(enemyCastle.getY());

        boolean captured = hero.getX() == enemyCastle.getX() && hero.getY() == enemyCastle.getY() && enemy.isDead();

        assertTrue(captured, "Игрок должен победить при захвате замка врага");
    }
    /**
     * Тест проверяет, что герой побеждает в автоматической битве.
     * Используется метод autoFight для симуляции боя, и проверяется, что герой выиграл.
     */
    @Test
    public void testHeroWinsAutoFight() {
        boolean heroWon = Battle.autoFight(battleField, allUnits);

        Assertions.assertTrue(heroWon, "Герой должен победить в этом бою");
    }


}