package org.example.game.build;

import org.example.game.person.Character;
import org.example.game.person.Hero;
import org.example.game.person.NPC;
import org.example.game.person.Team;
import org.example.game.person.Unit;
import org.example.game.map.Position;

import java.util.HashMap;
import java.util.Map;

public class Hotel extends ServiceBuilding {
    private static final String HOTEL_NAME = "Отель «У погибшего альпиниста»";
    private static final int ROOMS = 5;
    
    public Hotel() {
        super(HOTEL_NAME, ROOMS);
        initializeServices();
        initializeRegularVisitors();
    }
    
    private void initializeServices() {
        // Короткий отдых: +2 здоровья за 1 день
        Map<String, Integer> shortRestBonuses = new HashMap<>();
        shortRestBonuses.put("health", 2);
        addService(new Service("Короткий отдых", 48 * 60, 50, shortRestBonuses)); // 48 часов = 2 дня
        
        // Длинный отдых: +3 здоровья за 3 дня
        Map<String, Integer> longRestBonuses = new HashMap<>();
        longRestBonuses.put("health", 3);
        addService(new Service("Длинный отдых", 144 * 60, 120, longRestBonuses)); // 144 часа = 6 дней
    }
    
    private void initializeRegularVisitors() {
        // Создаем большую группу NPC с частотой посещений каждые 5 секунд (5 минут игрового времени)
        
        // Группа 1: Очень частые посетители (каждые 5 секунд)
        addRegularVisitor(new NPC("Местный житель", "Охотник", 1, Team.NEUTRAL, 70, 15, 4, 4, 1, new Position(0, 0), 5));
        addRegularVisitor(new NPC("Лесник", "Смотритель", 2, Team.NEUTRAL, 100, 20, 6, 6, 2, new Position(0, 0), 5));
        addRegularVisitor(new NPC("Дровосек", "Работник", 1, Team.NEUTRAL, 80, 15, 5, 5, 1, new Position(0, 0), 5));
        addRegularVisitor(new NPC("Рыбак", "Местный", 1, Team.NEUTRAL, 75, 15, 4, 4, 1, new Position(0, 0), 5));
        addRegularVisitor(new NPC("Пастух", "Фермер", 1, Team.NEUTRAL, 85, 15, 5, 5, 1, new Position(0, 0), 5));
        
        // Группа 2: Частые посетители (каждые 5 секунд)
        addRegularVisitor(new NPC("Старый альпинист", "Пенсионер", 1, Team.NEUTRAL, 100, 30, 5, 5, 2, new Position(0, 0), 5));
        addRegularVisitor(new NPC("Горный проводник", "Гид", 2, Team.NEUTRAL, 150, 40, 8, 8, 2, new Position(0, 0), 5));
        addRegularVisitor(new NPC("Турист", "Путешественник", 1, Team.NEUTRAL, 80, 20, 4, 4, 1, new Position(0, 0), 5));
        addRegularVisitor(new NPC("Краевед", "Историк", 2, Team.NEUTRAL, 110, 25, 6, 6, 1, new Position(0, 0), 5));
        addRegularVisitor(new NPC("Фотограф", "Художник", 1, Team.NEUTRAL, 90, 25, 5, 5, 1, new Position(0, 0), 5));
        
        // Группа 3: Средняя частота (каждые 5 секунд)
        addRegularVisitor(new NPC("Горный спасатель", "Спасатель", 3, Team.NEUTRAL, 200, 50, 10, 10, 2, new Position(0, 0), 5));
        addRegularVisitor(new NPC("Метеоролог", "Ученый", 2, Team.NEUTRAL, 120, 35, 6, 6, 1, new Position(0, 0), 5));
        addRegularVisitor(new NPC("Геолог", "Исследователь", 3, Team.NEUTRAL, 180, 45, 9, 9, 2, new Position(0, 0), 5));
        addRegularVisitor(new NPC("Биолог", "Ученый", 2, Team.NEUTRAL, 130, 30, 7, 7, 1, new Position(0, 0), 5));
        addRegularVisitor(new NPC("Археолог", "Исследователь", 2, Team.NEUTRAL, 140, 35, 7, 7, 1, new Position(0, 0), 5));
        
        // Группа 4: Редкие посетители (каждые 5 секунд)
        addRegularVisitor(new NPC("Торговец", "Купец", 2, Team.NEUTRAL, 160, 40, 8, 8, 1, new Position(0, 0), 5));
        addRegularVisitor(new NPC("Картограф", "Исследователь", 2, Team.NEUTRAL, 150, 35, 7, 7, 1, new Position(0, 0), 5));
        addRegularVisitor(new NPC("Альпинист", "Спортсмен", 3, Team.NEUTRAL, 190, 45, 9, 9, 2, new Position(0, 0), 5));
        addRegularVisitor(new NPC("Экскурсовод", "Гид", 2, Team.NEUTRAL, 130, 30, 6, 6, 1, new Position(0, 0), 5));
        addRegularVisitor(new NPC("Охотник", "Промысловик", 2, Team.NEUTRAL, 170, 40, 8, 8, 2, new Position(0, 0), 5));

        // Добавляем еще 10 NPC с частотой посещений каждые 5 секунд
        addRegularVisitor(new NPC("Странник", "Путешественник", 1, Team.NEUTRAL, 70, 15, 4, 4, 1, new Position(0, 0), 5));
        addRegularVisitor(new NPC("Отшельник", "Местный", 2, Team.NEUTRAL, 90, 25, 5, 5, 1, new Position(0, 0), 5));
        addRegularVisitor(new NPC("Травник", "Целитель", 1, Team.NEUTRAL, 80, 20, 4, 4, 1, new Position(0, 0), 5));
        addRegularVisitor(new NPC("Собиратель", "Фермер", 1, Team.NEUTRAL, 75, 15, 4, 4, 1, new Position(0, 0), 5));
        addRegularVisitor(new NPC("Проводник", "Гид", 2, Team.NEUTRAL, 100, 30, 6, 6, 2, new Position(0, 0), 5));
        addRegularVisitor(new NPC("Охотник за сокровищами", "Искатель", 2, Team.NEUTRAL, 120, 35, 7, 7, 2, new Position(0, 0), 5));
        addRegularVisitor(new NPC("Горный житель", "Местный", 1, Team.NEUTRAL, 85, 20, 5, 5, 1, new Position(0, 0), 5));
        addRegularVisitor(new NPC("Странствующий торговец", "Купец", 2, Team.NEUTRAL, 150, 40, 8, 8, 1, new Position(0, 0), 5));
        addRegularVisitor(new NPC("Горный мудрец", "Старейшина", 3, Team.NEUTRAL, 180, 45, 9, 9, 2, new Position(0, 0), 5));
        addRegularVisitor(new NPC("Странствующий бард", "Музыкант", 1, Team.NEUTRAL, 90, 25, 5, 5, 1, new Position(0, 0), 5));
    }
    
    @Override
    protected void applyServiceEffects(Character visitor, Service service) {
        Map<String, Integer> bonuses = service.getBonuses();
        
        // Применяем бонусы ко всем юнитам героя
        if (visitor.getUnits() != null) {
            for (Unit unit : visitor.getUnits()) {
                if (bonuses.containsKey("health")) {
                    // Используем takeDamage с отрицательным значением для увеличения здоровья
                    unit.takeDamage(-bonuses.get("health"));
                }
            }
        }
        
        // Применяем бонусы к самому герою
        if (bonuses.containsKey("health")) {
            // Используем takeDamage с отрицательным значением для увеличения здоровья
            visitor.takeDamage(-bonuses.get("health"));
        }
        
        System.out.println(visitor.getName() + " получил бонусы от услуги " + service.getName() + ":");
        for (Map.Entry<String, Integer> bonus : bonuses.entrySet()) {
            System.out.println("- " + bonus.getKey() + ": +" + bonus.getValue());
        }
    }
    
    public String getDescription() {
        return "Отель расположен в живописном месте у подножия гор. " +
               "Предлагает уютные номера и различные услуги для отдыха. " +
               "Максимальное количество гостей: " + maxVisitors + ". " +
               "Регулярно посещается местными жителями и путешественниками.";
    }

    // Обновляем позиции NPC при изменении позиции отеля
    public void updateNPCPositions(Position newPosition) {
        for (NPC npc : regularVisitors) {
            npc.setPosition(newPosition.getX(), newPosition.getY());
        }
    }
} 