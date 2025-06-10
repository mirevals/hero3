package org.example.game.weapons;

import java.io.Serializable;

public class AttackRange implements Serializable {
    private static final long serialVersionUID = 1L;
    private int range;

    public AttackRange(int range) {
        this.range = range;
    }

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }
} 