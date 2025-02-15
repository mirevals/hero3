package org.example.entities.buildings;

import org.example.entities.units.Unit;
import org.example.entities.units.UnitFactory;
import org.example.entities.units.Team;

public class UnitBuilding extends Building {
    private final UnitFactory unitFactory;

    public UnitBuilding(String name, int cost, UnitFactory unitFactory) {
        super(name, cost);
        this.unitFactory = unitFactory;
    }

    @Override
    public void build() {
        System.out.println("Здание для найма юнитов построено.");
    }

    public Unit recruitUnit(String unitType, Team team) {
        // Рекрутируем юнитов в зависимости от типа здания
        System.out.println("Юнит набран: " + unitFactory.createUnit(unitType, team).getName());
        return unitFactory.createUnit(unitType, team);  // Передаем unitType и команду
    }
}