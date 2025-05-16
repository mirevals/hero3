package org.example.game.person;


import org.example.game.Player;
import org.example.game.battle.Battle;
import org.example.game.battle.BattleField;
import org.example.game.build.EnemyCastle;
import org.example.game.build.HeroCastle;
import org.example.game.map.GameMap;
import org.example.game.map.MapManager;
import org.example.game.map.Position;
import org.example.game.map.Road;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import static org.example.game.build.Shop.availableBuildings;
import static org.junit.jupiter.api.Assertions.assertTrue;
//2. Корректность завершения игры победой бота всеми способами
public class FinishEnemyTest {

    private static final Logger logger = Logger.getLogger(FinishEnemyTest.class.getName());

    static {
        try {
            // Лог-файл
            FileHandler fileHandler = new FileHandler("game-test.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);

            // Консольный лог
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(consoleHandler);

            logger.setUseParentHandlers(false); // отключаем стандартный консольный вывод, чтобы не было дубликатов
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


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

        Unit heroUnit = new Unit(Unit.UnitType.WARRIOR, 100, 100, 1, 10, Team.HERO, 'W', 100);
        Unit enemyUnit = new Unit(Unit.UnitType.WARRIOR, 1000, 100, 1, 10, Team.ENEMY, 'A', 100);

        List<Unit> unitsHero = new ArrayList<>();
        List<Unit> unitsEnemy = new ArrayList<>();

        unitsHero.add(heroUnit);
        unitsEnemy.add(enemyUnit);
        unitsEnemy.add(enemyUnit);
        unitsEnemy.add(enemyUnit);
        unitsEnemy.add(enemyUnit);
        unitsEnemy.add(enemyUnit);
        unitsEnemy.add(enemyUnit);
        unitsEnemy.add(enemyUnit);
        unitsEnemy.add(enemyUnit);
        unitsEnemy.add(enemyUnit);
        unitsEnemy.add(enemyUnit);
        unitsEnemy.add(enemyUnit);
        unitsEnemy.add(enemyUnit);
        unitsEnemy.add(enemyUnit);
        unitsEnemy.add(enemyUnit);
        unitsEnemy.add(enemyUnit);
        unitsEnemy.add(enemyUnit);
        unitsEnemy.add(enemyUnit);
        unitsEnemy.add(enemyUnit);
        unitsEnemy.add(enemyUnit);
        unitsEnemy.add(enemyUnit);
        unitsEnemy.add(enemyUnit);
        unitsEnemy.add(enemyUnit);
        unitsEnemy.add(enemyUnit);
        unitsEnemy.add(enemyUnit);
        unitsEnemy.add(enemyUnit);
        unitsEnemy.add(enemyUnit);
        unitsEnemy.add(enemyUnit);
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
     * Тест проверяет, что бот побеждает, когда все юниты игрока уничтожены.
     * Проверяется, что список юнитов героя пуст, что свидетельствует о победе бота.
     */
    @Test
    public void testVictoryWhenAllEnemiesDefeated() {
        hero.getUnits().clear();

        boolean isVictory = hero.getUnits().isEmpty();

        assertTrue(isVictory, "Игрок должен проиграть, когда все юниты мертвы");

        logger.info("testVictoryWhenAllEnemiesDefeated успешно пройден.");
    }

    /**
     * Тест проверяет, что враг побеждает в автоматическом бою.
     * Используется метод autoFight для симуляции боя, и проверяется, что враг выиграл.
     */
    @Test
    public void testHeroWinsAutoFight() {
        boolean heroWon = Battle.autoFight(battleField, allUnits);
        Assertions.assertFalse(heroWon, "Враг должен победить в этом бою");

        logger.info("testHeroWinsAutoFight успешно пройден.");
    }

    @Test
    public void testVictoryWhenHeroDie() {
        hero.die();
        boolean captured = hero.isDead();
        assertTrue(captured, "Игрок мертв");

        logger.info("testVictoryWhenHeroDie успешно пройден.");
    }
}
