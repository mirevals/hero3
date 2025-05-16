package org.example.game;

import java.io.Serializable;
import org.example.game.score.Record;

public class GameState implements Serializable {
    private static final long serialVersionUID = 1L;
    
    // Добавляем поля для отслеживания статистики
    private int enemiesDefeated = 0;
    private int castlesCaptured = 0;
    private int turnsCompleted = 0;
    private int unitsLost = 0;
    private String currentMapName;

    // Добавляем методы для обновления статистики
    public void incrementEnemiesDefeated() {
        enemiesDefeated++;
    }

    public void incrementCastlesCaptured() {
        castlesCaptured++;
    }

    public void incrementUnitsLost() {
        unitsLost++;
    }

    public void incrementTurns() {
        turnsCompleted++;
    }

    public void setCurrentMapName(String mapName) {
        this.currentMapName = mapName;
    }

    public Record generateRecord(String playerName) {
        return new Record(playerName, currentMapName, enemiesDefeated, 
                         castlesCaptured, turnsCompleted, unitsLost);
    }

    // Геттеры для статистики
    public int getEnemiesDefeated() {
        return enemiesDefeated;
    }

    public int getCastlesCaptured() {
        return castlesCaptured;
    }

    public int getTurnsCompleted() {
        return turnsCompleted;
    }

    public int getUnitsLost() {
        return unitsLost;
    }

    public String getCurrentMapName() {
        return currentMapName;
    }
} 