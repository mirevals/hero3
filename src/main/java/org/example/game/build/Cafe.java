package org.example.game.build;

import org.example.game.build.Service;
import org.example.game.build.ServiceEffect;
import java.util.concurrent.ConcurrentHashMap;
import java.io.IOException;

public class Cafe extends Building {
    private static final int MAX_VISITORS = 3;
    private static final long serialVersionUID = 4116052731001905656L;

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        // Инициализируем услуги после десериализации
        initializeServices();
    }

    public Cafe() {
        super("Сырники от тети Глаши", false);
        this.maxVisitors = MAX_VISITORS;
        initializeServices();
    }

    private void initializeServices() {
        this.services.clear();
        services.put("coffee", new Service(
            "Кофе",
            30, // 30 minutes
            50,
            new ServiceEffect(ServiceEffect.EffectType.MOVEMENT_BOOST, 1)
        ));

        services.put("meal", new Service(
            "Питание",
            60, // 1 hour
            100,
            new ServiceEffect(ServiceEffect.EffectType.HEALTH_BOOST, 1)
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