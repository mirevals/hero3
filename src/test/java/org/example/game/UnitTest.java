package org.example.game;

import org.example.game.map.Position;
import org.example.game.person.Team;
import org.example.game.person.Unit;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UnitTest {
    @Test
    public void testUnitCannotAttackAlly() {
        // Создаем двух юнитов одной команды
        Unit heroUnit1 = new Unit(Unit.UnitType.WARRIOR, 100, 10, 5, 3, Team.HERO, new Position(1, 1));
        Unit heroUnit2 = new Unit(Unit.UnitType.ARCHER, 80, 8, 4, 5, Team.HERO, new Position(2, 2));

        // Пробуем, чтобы первый юнит атаковал второго юнита своей команды
        heroUnit1.attack(heroUnit2);

        // Проверяем, что атакующий юнит не смог атаковать союзника
        // Ожидаем, что будет напечатано сообщение: "Юнит не может атаковать юнитов своей команды."
        // Также можно проверить, что здоровье второго юнита не изменилось.
        assertEquals(80, heroUnit2.getHealth(), "Здоровье союзника не должно измениться.");
    }
}
