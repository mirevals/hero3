package org.example.game;

import org.example.game.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class HeroTest {


    private GameMap gameMap;

    @Test
    public void testHeroHasUnitOnCreation() {
        gameMap = new GameMap(9, 5);

        Position startPosition = new Position(gameMap.getHeroX(), gameMap.getHeroY()); // Начальная позиция героя на карте
        Team team = Team.PLAYER;  // Создаем команду героя
        // Создаем юнита
        Unit heroUnit = new Unit(Unit.UnitType.WARRIOR, 100, 10, 5, 3, team, startPosition);

        // Создаем героя с юнитом
        Hero hero = new Hero("Hero1", 5, new Position(0, 0), heroUnit, Team.PLAYER);

        // Проверяем, что у героя есть юнит
        assertNotNull(hero.getUnit(),"Герой должен иметь юнита");

        // Проверяем, что имя юнита соответствует ожидаемому
        assertEquals("WARRIOR", hero.getUnit().getName(), "Имя юнита должно быть 'Warrior'");

        // Проверяем, что у юнита есть здоровье
        assertTrue(hero.getUnit().getHealth() > 0, "Юнит должен иметь положительное количество здоровья");
    }


    @Test
    public void testHeroUnitHealthAfterDamage() {
        gameMap = new GameMap(9, 5);
        Position startPosition = new Position(gameMap.getHeroX(), gameMap.getHeroY()); // Начальная позиция героя на карте
        Team team = Team.PLAYER;  // Создаем команду героя
        // Создаем юнита
        Unit heroUnit = new Unit(Unit.UnitType.WARRIOR, 100, 10, 5, 3, team, startPosition);

        // Создаем героя с юнитом
        Hero hero = new Hero("Hero1", 5, new Position(0, 0), heroUnit, Team.PLAYER);
        // Наносим урон юниту
        hero.getUnit().takeDamage(10);

        // Проверяем, что здоровье юнита уменьшилось
        assertEquals(90, hero.getUnit().getHealth(),"Здоровье юнита должно быть уменьшено на 10");
    }


}