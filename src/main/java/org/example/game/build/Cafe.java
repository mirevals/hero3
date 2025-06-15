package org.example.game.build;

import org.example.game.build.Service;
import org.example.game.build.ServiceEffect;
import org.example.game.build.ServiceEffect.EffectType;
import java.util.concurrent.ConcurrentHashMap;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class Cafe extends Building {
    private static final int MAX_VISITORS = 3;
    private static final long serialVersionUID = 4116052731001905656L;
    private final ConcurrentHashMap<String, Service> services;

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        initializeServices();
    }

    public Cafe() {
        super("Сырники от тети Глаши", false);
        this.maxVisitors = MAX_VISITORS;
        this.services = new ConcurrentHashMap<>();
        initializeServices();
    }

    private void initializeServices() {
        services.clear();
        services.put("coffee", new Service(
            "Кофе",
            50,
            5,
            new ServiceEffect(EffectType.MOVEMENT_BOOST, 2)
        ));

        services.put("meal", new Service(
            "Питание",
            100,
            15,
            new ServiceEffect(EffectType.HEALTH_BOOST, 30)
        ));
    }

    public List<Service> getServices() {
        return new ArrayList<>(services.values());
    }

    public List<Service> getAvailableServices() {
        return getServices();
    }

    @Override
    public boolean canPlaceAt(int x, int y, char[][] map) {
        return super.canPlaceAt(x, y, map);
    }

    public char getSymbol() {
        return 'K';
    }

    public int getMaxVisitors() {
        return MAX_VISITORS;
    }
} 