package org.example.game.build;

import java.util.concurrent.ConcurrentHashMap;
import java.io.IOException;

public class BarberShop extends Building {
    private static final int MAX_VISITORS = 2; // 2 specialists
    private static final long serialVersionUID = 9075732562272709601L;

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
        initializeServices();
    }

    private void initializeServices() {
        System.out.println("Debug: initializeServices called");
        System.out.println("Debug: services before clear: " + (this.services == null ? "null" : "not null"));
        this.services.clear();
        System.out.println("Debug: services after clear: " + (this.services == null ? "null" : "not null"));
        
        // Простая стрижка - без бонусов
        Service simpleCut = new Service(
            "Просто стрижка",
            10,  // 10 minutes
            30,  // 30 gold
            null  // без эффекта
        );
        this.services.put("simple_cut", simpleCut);
        System.out.println("Debug: Added simple_cut service");

        // Модная стрижка - сокращение времени захвата замка
        Service fashionCut = new Service(
            "Модная стрижка",
            30,  // 30 minutes
            100, // 100 gold
            new ServiceEffect(ServiceEffect.EffectType.CASTLE_CAPTURE_TIME_REDUCTION, 1)
        );
        this.services.put("fashion_cut", fashionCut);
        System.out.println("Debug: Added fashion_cut service");
        System.out.println("Debug: Final services size: " + this.services.size());
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