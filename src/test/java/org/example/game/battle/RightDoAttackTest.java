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
import static org.junit.jupiter.api.Assertions.assertEquals;
//4. Корректность дальности атаки
public class RightDoAttackTest {

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


        Road road = new Road(2, 2, 8, 2);
        mapManager = new MapManager(heroCastle, enemyCastle, enemy, hero, gameMap, road, carriage);
        battleField = new BattleField(allUnits);

    }

    /**
     * Тест проверяет, что герой останавливается на враге, если тот находится в пределах его дальности атаки.
     * Герой должен двигаться к врагу и остановиться, когда враг окажется в радиусе действия героя.
     */
    @Test
    public void testHeroStopsWhenEnemyInRange() {

        // Устанавливаем начальные позиции героя и врага
        hero.setX(3); // Начальная позиция героя (X)
        hero.setY(3); // Начальная позиция героя (Y)

        enemy.setX(5); // Устанавливаем врага на расстоянии 2 клетки по оси X от героя
        enemy.setY(5); // Устанавливаем врага на расстоянии 2 клетки по оси Y от героя

        // Проверим, что герой может двигаться в сторону врага и остановится, если он в пределах дальности хода
        // Герой двигается на 3 клетки (в пределах его дальности хода)
        mapManager.moveHero(1, 1, 3, hero, enemy, heroCastle, player, enemyCastle, heroCastle, gameMap, mapManager, buyUnit, null, battleField, allUnits, carriage);

        // Проверка, что герой остановился на враге, так как враг в пределах дальности хода
        assertEquals(5, hero.getX(), "Герой должен был остановиться на враге.");
        assertEquals(5, hero.getY(), "Герой должен был остановиться на враге.");
    }

//5. Корректность совершения атаки

    /**
     * Тест проверяет корректность механизма получения урона героями и врагами.
     * Герой и враг должны потерять здоровье на определенное количество единиц урона.
     * После получения урона, их здоровье должно уменьшиться на соответствующую величину.
     */
    @Test
    public void testTakeDamage() {
        Unit heroUnit = new Unit(Unit.UnitType.WARRIOR, 100, 100, 1, 10, Team.HERO, 'W', 100);
        Unit enemyUnit = new Unit(Unit.UnitType.WARRIOR, 100, 100, 1, 10, Team.ENEMY, 'A', 100);

        List<Unit> unitsHero = new ArrayList<>();
        List<Unit> unitsEnemy = new ArrayList<>();

        unitsHero.add(heroUnit);

        unitsEnemy.add(enemyUnit);
        Hero hero1 = new Hero("Герой", 10, Team.HERO, 1000, 10, 10, 100, 100, 100, 3, unitsHero);
        Enemy enemy1 = new Enemy("Враг", 5, Team.ENEMY, 100, 10, 10, 100, 100, 100, 1, unitsEnemy);

        // Враг получает 30 единиц урона без защиты
        hero1.takeDamage(30);
        enemy1.takeDamage(30);

        // Проверяем, что здоровье героя уменьшилось на 30
        assertEquals(70, hero1.getHealth(), "Герой должен был потерять 30 здоровья.");
        assertEquals(70, enemy1.getHealth(), "Герой должен был потерять 30 здоровья.");
    }
}
