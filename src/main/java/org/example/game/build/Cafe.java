package org.example.game.build;

import org.example.game.person.Character;
import org.example.game.person.Hero;
import org.example.game.person.Unit;

import java.util.HashMap;
import java.util.Map;

public class Cafe extends ServiceBuilding {
    public Cafe() {
        super("Кафе «Сырники от тети Глаши»", 12); // 3 waiters * 4 visitors
        
        // Initialize available services
        Map<String, Integer> quickSnackBonus = new HashMap<>();
        quickSnackBonus.put("moveRange", 2);
        availableServices.add(new Service("Просто перекус", 15, 30, quickSnackBonus)); // 15 minutes
        
        Map<String, Integer> fullLunchBonus = new HashMap<>();
        fullLunchBonus.put("moveRange", 3);
        availableServices.add(new Service("Плотный обед", 30, 60, fullLunchBonus)); // 30 minutes
    }
    
    @Override
    protected void applyServiceEffects(Character visitor, Service service) {
        if (visitor instanceof Hero) {
            Hero hero = (Hero) visitor;
            Map<String, Integer> bonuses = service.getBonuses();
            
            // Apply movement bonus to all hero's units
            for (Unit unit : hero.getUnits()) {
                int moveBonus = bonuses.getOrDefault("moveRange", 0);
                // Increase move range for the current turn
                unit.move(); // This will use the increased move range
            }
            
            System.out.println(visitor.getName() + " завершил " + service.getName() + 
                             " в кафе. Все юниты получили +" + 
                             bonuses.get("moveRange") + " к дальности перемещения.");
        }
    }
} 