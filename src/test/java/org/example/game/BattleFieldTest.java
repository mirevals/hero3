package org.example.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BattleFieldTest {

    private BattleField battleField;

    @BeforeEach
    public void setUp() {
        // Создание юнитов для теста

        // Создаем несколько юнитов с различными параметрами
        Unit warrior = new Unit(Unit.UnitType.WARRIOR, 100, 15, 3, 1, Team.HERO, new Position(0, 0));
        Unit archer = new Unit(Unit.UnitType.ARCHER, 75, 10, 4, 3, Team.HERO, new Position(1, 1));
        Unit mage = new Unit(Unit.UnitType.MAGE, 50, 20, 2, 5, Team.HERO, new Position(2, 2));

        Unit enemyWarrior = new Unit(Unit.UnitType.WARRIOR, 100, 12, 3, 1, Team.ENEMY, new Position(4, 4));
        Unit enemyArcher = new Unit(Unit.UnitType.ARCHER, 80, 8, 4, 3, Team.ENEMY, new Position(3, 3));

        // Добавляем юнитов в списки
        List<Unit> heroUnits = new ArrayList<>();
        heroUnits.add(warrior);
        heroUnits.add(archer);
        heroUnits.add(mage);

        List<Unit> enemyUnits = new ArrayList<>();
        enemyUnits.add(enemyWarrior);
        enemyUnits.add(enemyArcher);

        // Инициализация поля с учетом юнитов
        battleField = new BattleField(heroUnits, enemyUnits);
    }

    // Тест на правильное размещение препятствий
    @Test
    public void testBattleFieldWithUnits() {
        // Создаем юнитов героя и противника
        List<Unit> heroUnits = new ArrayList<>();
        List<Unit> enemyUnits = new ArrayList<>();

        // Заполняем списки юнитами (создаем юнитов с нужными параметрами)
        heroUnits.add(new Unit(Unit.UnitType.WARRIOR, 100, 10, 3, 1, Team.HERO, new Position(0, 0)));
        enemyUnits.add(new Unit(Unit.UnitType.ARCHER, 80, 15, 2, 2, Team.ENEMY, new Position(4, 4)));

        // Создаем поле сражения с размерами 5x5 и юнитами
        BattleField battleField = new BattleField( heroUnits, enemyUnits);

        // Теперь можно делать проверки
        assertTrue(battleField.getField()[0][0].hasObstacle(), "Препятствие должно быть на позиции (0, 0)");
        assertTrue(battleField.getField()[4][4].hasObstacle(), "Препятствие должно быть на позиции (4, 4)");
    }

    @Test
    public void testUnitPlacement() {
        // Создаем список юнитов (например, 1 юнит)
        List<Unit> heroUnits = new ArrayList<>();
        Unit unit = new Unit(Unit.UnitType.WARRIOR, 100, 10, 5, 3, Team.HERO, new Position(0, 0));
        heroUnits.add(unit);

        // Создаем поле с размерами, зависящими от количества юнитов
        BattleField battleField = new BattleField(heroUnits, new ArrayList<>());  // Противники не добавляются в этот тест

        // Проверяем, что клетка (1, 1) содержит препятствие, так как это должно быть
        assertTrue(battleField.getField()[1][1].hasObstacle(), "На клетке (1, 1) должно быть препятствие");

        // Размещаем юнита в свободной клетке
        assertTrue(battleField.placeUnit(unit, 1, 2), "Юнит должен быть размещен на поле");

        // Проверяем, что юнит действительно размещен на клетке (1, 2)
        assertEquals(unit.getPosition(), new Position(1, 2), "Юнит должен находиться на клетке (1, 2)");

        // Пробуем разместить юнита на клетке с препятствием (например, на клетке (1, 1))
        assertFalse(battleField.placeUnit(unit, 1, 1), "Юнит не должен быть размещен на клетке с препятствием");
    }

    @Test
    public void testPrintField() {
        // Создаем юнитов
        Unit heroUnit = new Unit(Unit.UnitType.WARRIOR, 100, 10, 5, 3, Team.HERO, new Position(0, 0));
        Unit enemyUnit = new Unit(Unit.UnitType.WARRIOR, 100, 10, 5, 3, Team.ENEMY, new Position(0, 0));

        // Размещаем юнитов на поле
        battleField.placeUnit(heroUnit, 1, 1);
        battleField.placeUnit(enemyUnit, 2, 2);

        // Перехватываем вывод в консоль
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        System.setOut(printStream);

        // Печатаем поле
        battleField.printField();

        // Восстанавливаем вывод в консоль
        System.setOut(System.out);

        // Получаем вывод и проверяем, что юниты находятся на правильных позициях
        String fieldOutput = outputStream.toString();

        // Проверяем, что в поле есть символы юнитов на соответствующих позициях
        assertTrue(fieldOutput.contains("O O X O O"), "Ожидаемый вывод не совпадает с действительным");
        assertTrue(fieldOutput.contains("O O O O O"), "Ожидаемый вывод не совпадает с действительным");
    }
}