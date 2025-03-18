package org.example.game.build;
import org.example.game.person.Hero;
import org.example.game.person.Unit;
import org.example.game.person.Unit.UnitType;

import java.util.List;

public class GuardPost extends Building {

    public GuardPost() {
        super("Сторожевой пост", true);
    }

    // Метод для вывода доступных юнитов с номерами и стоимостью
    public static void displayAvailableUnits(List<Unit> buyUnit) {
        System.out.println("Доступные юниты для найма:");
        for (int i = 0; i < buyUnit.size(); i++) {
            Unit unit = buyUnit.get(i);
            System.out.println(i + 1 + ". " + unit.getType() + " - Стоимость: " + unit.getCost());
        }
    }

    public void buyUnit(List<Unit> buyUnit, List<Unit> unitsHero, Hero hero) {

    }
}
