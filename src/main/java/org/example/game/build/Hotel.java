package org.example.game.build;

import org.example.game.build.Service;
import org.example.game.build.ServiceEffect;
import java.util.concurrent.ConcurrentHashMap;

public class Hotel extends Building {
    private static final int MAX_VISITORS = 5;
    private ConcurrentHashMap<String, Service> services;

    public Hotel() {
        super("У погибшего альпиниста", false);
        initializeServices();
    }

    private void initializeServices() {
        services = new ConcurrentHashMap<>();
        services.put("short_rest", new Service(
            "Короткий отдых",
            1440, // 1 day in minutes
            100,
            new ServiceEffect(ServiceEffect.EffectType.HEALTH_BOOST, 2)
        ));

        services.put("long_rest", new Service(
            "Длинный отдых",
            4320, // 3 days in minutes
            250,
            new ServiceEffect(ServiceEffect.EffectType.HEALTH_BOOST, 3)
        ));
    }

    @Override
    public boolean canPlaceAt(int x, int y, char[][] map) {
        // Используем базовую реализацию - только проверка на пустую клетку
        return super.canPlaceAt(x, y, map);
    }

    public char getSymbol() {
        return 'O';
    }

    public int getMaxVisitors() {
        return MAX_VISITORS;
    }
} 