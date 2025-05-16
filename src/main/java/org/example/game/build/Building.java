package org.example.game.build;

import java.io.Serializable;

public class Building implements Serializable {
    private static final long serialVersionUID = 1L;

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