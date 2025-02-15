package org.example.entities.buildings;

import org.example.entities.units.Unit;

import java.util.ArrayList;
import java.util.List;

public class Barracks extends Building {
    private final List<Unit> recruitedUnits = new ArrayList<>();

    public Barracks(int cost) {
        super("Казарма", cost);
    }

    @Override
    public void build() {
        System.out.println("Казарма построена!");
    }

    public void recruitUnit(Unit unit) {
        recruitedUnits.add(unit);
        System.out.println(unit.getName() + " нанят!");
    }

    public List<Unit> getRecruitedUnits() {
        return recruitedUnits;
    }
}