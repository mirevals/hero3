package org.example.game.person;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public enum Team implements Serializable {
    HERO("Player"),
    ENEMY("Enemy");

    private final String name;
    private final List<Unit> units = new ArrayList<>();

    Team(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addUnit(Unit unit) {
        units.add(unit);
    }

    public void removeUnit(Unit unit) {
        units.remove(unit);
    }

    // Удаление всех юнитов команды
    public void removeAllUnits() {
        units.clear();
    }

    public boolean hasUnits() {
        return !units.isEmpty();
    }
}