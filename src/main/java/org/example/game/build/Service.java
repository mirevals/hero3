package org.example.game.build;

import java.io.Serializable;

public class Service implements Serializable {
    private String name;
    private int durationMinutes;
    private int goldCost;
    private ServiceEffect effect;

    public Service(String name, int durationMinutes, int goldCost, ServiceEffect effect) {
        this.name = name;
        this.durationMinutes = durationMinutes;
        this.goldCost = goldCost;
        this.effect = effect;
    }

    public String getName() {
        return name;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public int getGoldCost() {
        return goldCost;
    }

    public ServiceEffect getEffect() {
        return effect;
    }

    public long getDurationMs() {
        return durationMinutes * 100L; // 1 minute = 100ms
    }

    @Override
    public String toString() {
        return String.format("%s (Duration: %d min, Cost: %d gold)", name, durationMinutes, goldCost);
    }
} 