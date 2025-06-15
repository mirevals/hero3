package org.example.game.build;

import java.io.Serializable;

public class Service implements Serializable {
    private static final long serialVersionUID = -602029190897193349L;
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
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(" — ");
        if (effect != null) {
            sb.append(effect.toString());
        } else {
            sb.append("без бонусов");
        }
        sb.append(", длительность: ");
        if (durationMinutes % 1440 == 0) {
            int days = durationMinutes / 1440;
            sb.append(days).append(" ").append(days == 1 ? "день" : (days >= 2 && days <= 4 ? "дня" : "дней"));
        } else {
            sb.append(durationMinutes).append(" минут");
        }
        sb.append(", цена: ").append(goldCost).append(" золота");
        return sb.toString();
    }
} 