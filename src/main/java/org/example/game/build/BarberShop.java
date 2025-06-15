package org.example.game.build;

import java.util.concurrent.ConcurrentHashMap;

public class BarberShop extends Building {
    private static final int MAX_VISITORS = 2; // 2 specialists
    private ConcurrentHashMap<String, Service> services;

    public BarberShop() {
        super("Barber Shop", false);
        this.symbol = 'B';
        this.maxVisitors = MAX_VISITORS;
        initializeServices();
    }

    private void initializeServices() {
        services = new ConcurrentHashMap<>();
        
        // Простая стрижка - без бонусов
        Service simpleCut = new Service(
            "Просто стрижка",
            10,  // 10 minutes
            30,  // 30 gold
            null  // без эффекта
        );
        services.put("simple_cut", simpleCut);

        // Модная стрижка - сокращение времени захвата замка
        Service fashionCut = new Service(
            "Модная стрижка",
            30,  // 30 minutes
            100, // 100 gold
            new ServiceEffect(ServiceEffect.EffectType.CASTLE_CAPTURE_TIME_REDUCTION, 1)
        );
        services.put("fashion_cut", fashionCut);
    }

    @Override
    public boolean canPlaceAt(int x, int y, char[][] map) {
        // Используем базовую реализацию - только проверка на пустую клетку
        return super.canPlaceAt(x, y, map);
    }

    public char getSymbol() {
        return 'B';
    }

    public int getMaxVisitors() {
        return MAX_VISITORS;
    }
} 