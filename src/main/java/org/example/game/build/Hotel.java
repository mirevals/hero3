package org.example.game.build;

import org.example.game.person.NPC;
import org.example.game.build.Service;
import org.example.game.build.ServiceEffect;
import org.example.game.build.ServiceEffect.EffectType;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.io.IOException;

public class Hotel extends Building {
    private static final long serialVersionUID = -7409586504057075246L;
    private static final int MAX_VISITORS = 5;
    private final ConcurrentHashMap<String, Service> services;

    public Hotel() {
        super("У погибшего альпиниста", false);
        this.maxVisitors = MAX_VISITORS;
        this.services = new ConcurrentHashMap<>();
        initializeServices();
    }

    private void initializeServices() {
        services.clear();
        services.put("hour_room", new Service("Номер на час", 50, 10, new ServiceEffect(EffectType.HEALTH_BOOST, 20)));
        services.put("night_room", new Service("Номер на ночь", 200, 12, new ServiceEffect(EffectType.HEALTH_BOOST, 50)));
        services.put("luxury_room", new Service("Люкс-номер", 500, 15, new ServiceEffect(EffectType.HEALTH_BOOST, 100)));
    }

    public List<Service> getServices() {
        return new ArrayList<>(services.values());
    }

    public List<Service> getAvailableServices() {
        return getServices();
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        initializeServices();
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