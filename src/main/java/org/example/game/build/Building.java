package org.example.game.build;

public class Building {
    private String name;
    private boolean canRecruitUnits;

    public Building(String name, boolean canRecruitUnits) {
        this.name = name;
        this.canRecruitUnits = canRecruitUnits;
    }

    public String getName() {
        return name;
    }

    public boolean canRecruitUnits() {
        return canRecruitUnits;
    }
}