package org.example.game.build;

import org.example.game.build.Service;
import org.example.game.build.ServiceEffect;
import java.util.concurrent.ConcurrentHashMap;

public class Cafe extends Building {
    private static final int MAX_VISITORS = 12; // 3 waiters * 4 visitors
    private ConcurrentHashMap<String, Service> services;

    public Cafe() {
        super("Сырники от тети Глаши", false);
        initializeServices();
    }

    private void initializeServices() {
        services = new ConcurrentHashMap<>();
        services.put("quick_snack", new Service(
            "Просто перекус",
            15, // 15 minutes
            50,
            new ServiceEffect(ServiceEffect.EffectType.MOVEMENT_BOOST, 2)
        ));

        services.put("full_meal", new Service(
            "Плотный обед",
            30, // 30 minutes
            100,
            new ServiceEffect(ServiceEffect.EffectType.MOVEMENT_BOOST, 3)
        ));
    }

    @Override
    public boolean canPlaceAt(int x, int y, char[][] map) {
        // Используем базовую реализацию - только проверка на пустую клетку
        return super.canPlaceAt(x, y, map);
    }

    public char getSymbol() {
        return 'K';
    }

    public int getMaxVisitors() {
        return MAX_VISITORS;
    }
} 