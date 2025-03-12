package org.example.game;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class CharacterTest {


    private GameMap gameMap;

    @Test
    public void testHeroHasUnitOnCreation() {
        gameMap = new GameMap(9, 5);

        Position startPosition = new Position(gameMap.getHeroX(), gameMap.getHeroY()); // Начальная позиция героя на карте
        Team team = Team.HERO;  // Создаем команду героя
        // Создаем юнита
        Unit heroUnit = new Unit(Unit.UnitType.WARRIOR, 100, 10, 5, 3, team, startPosition);

        // Создаем список юнитов для героя
        List<Unit> heroUnits = new ArrayList<>();
        heroUnits.add(heroUnit);

        // Создаем героя с юнитом
        Character character = new Character("Hero1", 5, new Position(0, 0), heroUnits, Team.HERO);

        // Проверяем, что у героя есть юнит
        assertNotNull(character.getUnit(), "Герой должен иметь юнита");

        // Проверяем, что имя юнита соответствует ожидаемому
        assertEquals("WARRIOR", character.getUnit().getName(), "Имя юнита должно быть 'Warrior'");

        // Проверяем, что у юнита есть здоровье
        assertTrue(character.getUnit().getHealth() > 0, "Юнит должен иметь положительное количество здоровья");
    }


    @Test
    public void testHeroUnitHealthAfterDamage() {
        gameMap = new GameMap(9, 5);
        Position startPosition = new Position(gameMap.getHeroX(), gameMap.getHeroY()); // Начальная позиция героя на карте
        Team team = Team.HERO;  // Создаем команду героя
        // Создаем юнита
        Unit heroUnit = new Unit(Unit.UnitType.WARRIOR, 100, 10, 5, 3, team, startPosition);

        // Создаем список юнитов для героя
        List<Unit> heroUnits = new ArrayList<>();
        heroUnits.add(heroUnit);

        // Создаем героя с переданным списком юнитов
        Character character = new Character("Hero1", 5, new Position(0, 0), heroUnits, Team.HERO);

        // Наносим урон юниту
        character.getUnit().takeDamage(10);

        // Проверяем, что здоровье юнита уменьшилось
        assertEquals(90, character.getUnit().getHealth(), "Здоровье юнита должно быть уменьшено на 10");
    }


}