package org.example.game.build;

import org.example.game.build.Service;
import org.example.game.build.ServiceEffect;
import org.example.game.build.ServiceEffect.EffectType;
import java.util.concurrent.ConcurrentHashMap;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class BarberShop extends Building {
    private static final int MAX_VISITORS = 2; // 2 specialists
    private static final long serialVersionUID = 9075732562272709601L;
    private final ConcurrentHashMap<String, Service> services;

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        // Инициализируем услуги после десериализации
        initializeServices();
    }

    public BarberShop() {
        super("Barber Shop", false);
        System.out.println("Debug: BarberShop constructor called");
        this.symbol = 'B';
        this.maxVisitors = MAX_VISITORS;
        this.services = new ConcurrentHashMap<>();
        initializeServices();
    }

    private void initializeServices() {
        services.clear();
        services.put("simple_cut", new Service(
            "Простая стрижка",
            10, // стоимость
            8, // длительность в секундах
            new ServiceEffect(EffectType.HEALTH_BOOST, 10)
        ));

        services.put("fashion_cut", new Service(
            "Модная стрижка",
            100, // стоимость
            12, // длительность в секундах
            new ServiceEffect(EffectType.CASTLE_CAPTURE_TIME_REDUCTION, 1)
        ));
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

    public List<Service> getServices() {
        return new ArrayList<>(services.values());
    }

    public List<Service> getAvailableServices() {
        return getServices();
    }
} 