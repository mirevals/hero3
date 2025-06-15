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
        
        // Simple haircut service
        Service simpleCut = new Service(
            "Simple Haircut",
            30,  // 30 minutes
            50,  // 50 gold
            new ServiceEffect(ServiceEffect.EffectType.HEALTH_BOOST, 10)
        );
        services.put("simple_cut", simpleCut);

        // Fashion haircut service
        Service fashionCut = new Service(
            "Fashion Haircut",
            45,  // 45 minutes
            100, // 100 gold
            new ServiceEffect(ServiceEffect.EffectType.HEALTH_BOOST, 25)
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