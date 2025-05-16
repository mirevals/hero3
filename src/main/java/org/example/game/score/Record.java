package org.example.game.score;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Record implements Serializable {
    private static final long serialVersionUID = 1L;

    private String playerName;
    private int score;
    private String mapName;
    private LocalDateTime dateTime;
    private int enemiesDefeated;
    private int castlesCaptured;
    private int turnsToComplete;
    private int unitsLost;

    public Record(String playerName, String mapName, int enemiesDefeated, 
                 int castlesCaptured, int turnsToComplete, int unitsLost) {
        this.playerName = playerName;
        this.mapName = mapName;
        this.enemiesDefeated = enemiesDefeated;
        this.castlesCaptured = castlesCaptured;
        this.turnsToComplete = turnsToComplete;
        this.unitsLost = unitsLost;
        this.dateTime = LocalDateTime.now();
        calculateScore();
    }

    private void calculateScore() {
        score = (enemiesDefeated * 100) + 
                (castlesCaptured * 500) + 
                (turnsToComplete < 50 ? 1000 : 0) - 
                (unitsLost * 50);
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getScore() {
        return score;
    }

    public String getMapName() {
        return mapName;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    @Override
    public String toString() {
        return String.format("Игрок: %-15s Очки: %-6d Карта: %-15s\n" +
                           "Враги: %-3d Замки: %-2d Ходы: %-3d Потери: %-2d",
                           playerName, score, mapName,
                           enemiesDefeated, castlesCaptured, turnsToComplete, unitsLost);
    }
} 