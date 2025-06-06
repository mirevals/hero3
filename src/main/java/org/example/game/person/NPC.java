package org.example.game.person;

import org.example.game.person.Team;
import org.example.game.map.Position;

public class NPC extends Character {
    private final String occupation; // Профессия или роль NPC (например, "Торговец", "Странник" и т.д.)
    private final int visitFrequency; // Как часто NPC посещает здания (в минутах)
    private long lastVisitTime; // Время последнего посещения здания

    public NPC(String name, String occupation, int level, Team team, int gold, int health, 
              int attack, int defense, int attackRange, Position position, int visitFrequency) {
        super(name, level, position, team, gold, health, attack, defense, attackRange, null);
        this.occupation = occupation;
        this.visitFrequency = visitFrequency;
        this.lastVisitTime = 0;
    }

    public String getOccupation() {
        return occupation;
    }

    public int getVisitFrequency() {
        return visitFrequency;
    }

    public long getLastVisitTime() {
        return lastVisitTime;
    }

    public void setLastVisitTime(long time) {
        this.lastVisitTime = time;
    }

    @Override
    public CharacterType getType() {
        return CharacterType.NPC;
    }

    @Override
    public String toString() {
        return getName() + " (" + occupation + ")";
    }
} 