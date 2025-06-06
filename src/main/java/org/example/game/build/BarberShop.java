package org.example.game.build;

import org.example.game.person.Character;
import org.example.game.person.Hero;

import java.util.HashMap;
import java.util.Map;

public class BarberShop extends ServiceBuilding {
    public BarberShop() {
        super("Парикмахерская «Отрезанное ухо»", 2); // 2 specialists
        
        // Initialize available services
        Map<String, Integer> simpleHaircutBonus = new HashMap<>();
        availableServices.add(new Service("Просто стрижка", 10, 20, simpleHaircutBonus)); // 10 minutes
        
        Map<String, Integer> fancyHaircutBonus = new HashMap<>();
        fancyHaircutBonus.put("castleCaptureTime", 1); // Reduces castle capture time from 2 to 1 turn
        availableServices.add(new Service("Модная стрижка", 30, 50, fancyHaircutBonus)); // 30 minutes
    }
    
    @Override
    protected void applyServiceEffects(Character visitor, Service service) {
        if (visitor instanceof Hero) {
            Hero hero = (Hero) visitor;
            Map<String, Integer> bonuses = service.getBonuses();
            
            if (service.getName().equals("Модная стрижка")) {
                // Apply castle capture time reduction
                int captureTimeReduction = bonuses.getOrDefault("castleCaptureTime", 0);
                // This effect will be applied when attempting to capture a castle
                System.out.println(visitor.getName() + " завершил " + service.getName() + 
                                 " в парикмахерской. Время захвата замка сокращено с 2 до 1 хода.");
            } else {
                System.out.println(visitor.getName() + " завершил " + service.getName() + 
                                 " в парикмахерской.");
            }
        }
    }
} 