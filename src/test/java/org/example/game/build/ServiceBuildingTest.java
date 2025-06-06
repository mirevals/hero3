package org.example.game.build;

import org.example.game.person.Character;
import org.example.game.person.Hero;
import org.example.game.person.NPC;
import org.example.game.person.Team;
import org.example.game.map.Position;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Queue;
import org.example.game.build.ServiceBuilding.ServiceRequest;
import org.example.game.build.ServiceBuilding.Service;

class ServiceBuildingTest {
    
    // Простая тестовая реализация ServiceBuilding
    private static class TestServiceBuilding extends ServiceBuilding {
        public TestServiceBuilding() {
            super("Test Building", 2); // 2 места максимум
            
            // Добавляем одну простую услугу для тестов
            Map<String, Integer> serviceBonus = new HashMap<>();
            addService(new Service("Test Service", 10, 20, serviceBonus));
        }
        
        @Override
        protected void applyServiceEffects(Character visitor, Service service) {
            // Пустая реализация для тестов
        }

        // Добавляем геттеры для тестирования
        public Map<Character, Service> getActiveServices() {
            return activeServices;
        }

        public Queue<ServiceRequest> getServiceQueue() {
            return serviceQueue;
        }

        public List<NPC> getRegularVisitors() {
            return regularVisitors;
        }
    }

    @Test
    void testStartService() {
        // Создаем все объекты для теста
        TestServiceBuilding building = new TestServiceBuilding();
        Hero hero = new Hero("TestHero", 10, Team.HERO, 1000, 10, 10, 100, 100, 100, 3, new ArrayList<>());
        
        // Тестируем начало услуги
        assertTrue(building.startService(hero, 0));
        assertEquals(1, building.getActiveServices().size());
        assertTrue(building.getActiveServices().containsKey(hero));
    }

    @Test
    void testQueue() {
        // Создаем все объекты для теста
        TestServiceBuilding building = new TestServiceBuilding();
        Hero hero = new Hero("TestHero", 10, Team.HERO, 1000, 10, 10, 100, 100, 100, 3, new ArrayList<>());
        Position pos = new Position(0, 0);
        NPC npc1 = new NPC("NPC1", "Citizen", 1, Team.NEUTRAL, 100, 10, 10, 100, 1, pos, 5);
        NPC npc2 = new NPC("NPC2", "Citizen", 1, Team.NEUTRAL, 100, 10, 10, 100, 1, pos, 5);
        
        // Заполняем здание
        building.startService(npc1, 0);
        building.startService(npc2, 0);
        
        // Пытаемся добавить героя в очередь
        assertTrue(building.startService(hero, 0));
        
        // Проверяем, что герой в очереди
        assertTrue(building.getServiceQueue().stream()
                .anyMatch(req -> req.getVisitor().equals(hero)));
    }

    @Test
    void testRegularVisitors() {
        // Создаем все объекты для теста
        TestServiceBuilding building = new TestServiceBuilding();
        Position pos = new Position(0, 0);
        NPC npc = new NPC("TestNPC", "Citizen", 1, Team.NEUTRAL, 100, 10, 10, 100, 1, pos, 5);
        
        // Тестируем добавление регулярного посетителя
        building.addRegularVisitor(npc);
        assertTrue(building.getRegularVisitors().contains(npc));
        
        // Тестируем удаление регулярного посетителя
        building.removeRegularVisitor(npc);
        assertFalse(building.getRegularVisitors().contains(npc));
    }

    @Test
    void testBuildingStatus() {
        // Создаем все объекты для теста
        TestServiceBuilding building = new TestServiceBuilding();
        Hero hero = new Hero("TestHero", 10, Team.HERO, 1000, 10, 10, 100, 100, 100, 3, new ArrayList<>());
        Position pos = new Position(0, 0);
        NPC npc = new NPC("TestNPC", "Citizen", 1, Team.NEUTRAL, 100, 10, 10, 100, 1, pos, 5);
        
        // Добавляем посетителей
        building.startService(npc, 0);
        building.startService(hero, 0);
        
        // Проверяем статус здания
        String status = building.getStatus();
        assertTrue(status.contains("Test Building"));
        assertTrue(status.contains(npc.getName()));
        assertTrue(status.contains(hero.getName()));
    }

    @Test
    void testServiceProperties() {
        // Создаем все объекты для теста
        TestServiceBuilding building = new TestServiceBuilding();
        Hero hero = new Hero("TestHero", 10, Team.HERO, 1000, 10, 10, 100, 100, 100, 3, new ArrayList<>());
        
        // Начинаем услугу
        building.startService(hero, 0);
        Service service = building.getActiveServices().get(hero);
        
        // Проверяем свойства услуги
        assertEquals("Test Service", service.getName());
        assertEquals(10, service.getDurationMinutes());
        assertEquals(20, service.getGoldCost());
    }
} 