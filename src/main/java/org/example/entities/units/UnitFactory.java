package org.example.entities.units;

public class UnitFactory {
    public Unit createUnit(String unitType, Team team) {
        switch (unitType) {
            case "Warrior":
                return new Unit("Воин", 100, 20, 3, 2, team);
            case "Archer":
                return new Unit("Лучник", 80, 15, 4, 3, team);
            case "Mage":
                return new Unit("Маг", 60, 25, 2, 5, team);
            default:
                throw new IllegalArgumentException("Неизвестный тип юнита: " + unitType);
        }
    }
}