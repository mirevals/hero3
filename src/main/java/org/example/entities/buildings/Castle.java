package org.example.entities.buildings;

import org.example.entities.Hero;
import org.example.entities.Position;


import java.util.ArrayList;
import java.util.List;

public class Castle {
    private final String name;
    private final Position position;
    private final List<Building> buildings;

    public Castle(String name, Position position) {
        this.name = name;
        this.position = position;
        this.buildings = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public Position getPosition() {
        return position;
    }

    public void addBuilding(Building building) {
        buildings.add(building);
    }

    public boolean hasUnitInCastle() {
        // Проверяем, есть ли юниты в замке
        for (Building building : buildings) {
            if (building instanceof UnitBuilding) {
                return true;
            }
        }
        return false;
    }

    public List<Building> getBuildings() {
        return buildings;
    }

    public boolean canBuildTavern() {
        return !hasUnitInCastle(); // Таверна может быть построена только если в замке нет юнитов
    }

    public void visitStable(Hero hero) {
        // Если герой посетил конюшню, увеличиваем его дальность перемещения
        for (Building building : buildings) {
            if (building instanceof Stable) {
                hero.increaseMovementRange(1); // Увеличиваем дальность перемещения на 1
                System.out.println(hero.getName() + " посетил конюшню и увеличил дальность перемещения!");
            }
        }
    }
}