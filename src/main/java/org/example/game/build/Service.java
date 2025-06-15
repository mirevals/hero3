package org.example.game.build;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

public class Service implements Serializable {
    private static final long serialVersionUID = -602029190897193349L;
    private static final AtomicInteger idCounter = new AtomicInteger(0);
    private final int id;
    private final String name;
    private final int cost;
    private final int durationMinutes;
    private final ServiceEffect effect;

    public Service(String name, int cost, int durationMinutes, ServiceEffect effect) {
        this.id = idCounter.incrementAndGet();
        this.name = name;
        this.cost = cost;
        this.durationMinutes = durationMinutes;
        this.effect = effect;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getCost() {
        return cost;
    }

    public int getDurationMinutes() {
        return durationMinutes;
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
        sb.append(", цена: ").append(cost).append(" золота");
        return sb.toString();
    }
} 