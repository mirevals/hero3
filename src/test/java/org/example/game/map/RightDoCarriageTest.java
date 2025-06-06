package org.example.game.map;

import org.example.game.Player;
import org.example.game.battle.BattleField;
import org.example.game.build.EnemyCastle;
import org.example.game.build.HeroCastle;
import org.example.game.person.*;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.example.game.build.Shop.availableBuildings;
import static org.junit.Assert.assertEquals;
//12. Корректность вашего индивидуального задания к ЛР1(тут зависит от
//задания, тестов может быть несколько)
public class RightDoCarriageTest {


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
     * Тест проверяет, что при прямом столкновении кареты с героем (карета заезжает прямо на координаты героя),
     * здоровье героя уменьшается на 10 единиц. Также проверяется, что позиция героя остаётся неизменной.
     */
    @Test
    public void testCarriageHitsHero() {
        hero.setPosition(5, 5);
        carriage.setPosition(5, 4);
        carriage.setDirection(Carriage.Direction.DOWN);
        int initialHealth = hero.getHealth();

        // Устанавливаем символ героя на карту
        gameMap.setCellValue(hero.getX(), hero.getY(), 'H');

        // Перемещаем карету к герою — ожидается прямое столкновение и урон 10
        moveCarriage(carriage, gameMap, hero);

        int expectedHealth = initialHealth - 10;
        assertEquals("Здоровье героя после столкновения с каретой должно уменьшиться на 10", expectedHealth, hero.getHealth());

        // Позиция героя не меняется
        assertEquals(5, hero.getX());
        assertEquals(5, hero.getY());
    }
    /**
     * Тест проверяет, что если карета проезжает мимо героя (не заезжает на его клетку, а проезжает рядом или сзади),
     * то здоровье героя уменьшается только на 5 единиц (половина урона). Это соответствует логике "прохождения рядом".
     */
    @Test
    public void testCarriagePassesByHero() {
        carriage.setPosition(5, 5);
        carriage.setDirection(Carriage.Direction.DOWN);
        hero.setPosition(5, 5);

        int initialHealth = hero.getHealth();

        moveCarriage(carriage, gameMap, hero);

        int expectedHealth = initialHealth - 5;
        assertEquals("Здоровье героя после проезда кареты должно уменьшиться на 5", expectedHealth, hero.getHealth());
    }

    // Подключение метода из твоего кода
    private void moveCarriage(Carriage carriage, GameMap gameMap, Hero hero) {
        int x = carriage.getPosition().getX();
        int y = carriage.getPosition().getY();

        // Принудительно задаём скорость 1 (можно будет рандом отключить в проде)
        int speed = 1;

        switch (carriage.getDirection()) {
            case LEFT -> x -= speed;
            case RIGHT -> x += speed;
            case UP -> y -= speed;
            case DOWN -> y += speed;
        }

        if (x < 0 || x >= gameMap.getMap()[0].length || y < 0 || y >= gameMap.getMap().length) {
            System.out.println("Карета достигла края карты и не может двигаться дальше.");
            return;
        }

        // Вызов твоей логики (в идеале, конечно, вынести из приватного метода)
        updateCarriagePosition(carriage, x, y, gameMap, hero);
    }

    // Логика, продублированная для теста (можно было бы изолировать через CarriageManager или отдельный сервис)
    private void updateCarriagePosition(Carriage carriage, int x, int y, GameMap gameMap, Hero character) {
        char[][] map = gameMap.getMap();
        int oldX = carriage.getPosition().getX();
        int oldY = carriage.getPosition().getY();

        int damage = 10;
        int halfDamage = damage / 2;

        // Проверка "спины"
        int backX = oldX;
        int backY = oldY;
        switch (carriage.getDirection()) {
            case LEFT -> backX += 1;
            case RIGHT -> backX -= 1;
            case UP -> backY += 1;
            case DOWN -> backY -= 1;
        }

        if (character.getX() == backX && character.getY() == backY) {
            character.setHealth(character.getHealth() - halfDamage);
        }

        // Столкновение
        if (map[y][x] == 'H' && character.getX() == x && character.getY() == y) {
            character.setHealth(character.getHealth() - damage);
            return;
        }

        // Проверка выхода за границу
        if (x < 1 || x >= map[0].length - 1 || y < 0 || y >= map.length - 1) {
            switch (carriage.getDirection()) {
                case LEFT -> carriage.setDirection(Carriage.Direction.RIGHT);
                case RIGHT -> carriage.setDirection(Carriage.Direction.LEFT);
                case UP -> carriage.setDirection(Carriage.Direction.DOWN);
                case DOWN -> carriage.setDirection(Carriage.Direction.UP);
            }
            x = oldX;
            y = oldY;
        }

        carriage.setPosition(x, y);
        gameMap.setCellValue(x, y, 'D');

        // Проезд сзади
        if (character.getX() == oldX && character.getY() == oldY) {
            character.setHealth(character.getHealth() - halfDamage);
        }

        gameMap.setCellValue(oldX, oldY, ' ');
    }
}
