package org.example.game;

import java.io.Serializable;
import java.util.List;

import org.example.game.build.EnemyCastle;
import org.example.game.build.HeroCastle;
import org.example.game.map.GameMap;
import org.example.game.map.Road;
import org.example.game.person.Carriage;
import org.example.game.person.Enemy;
import org.example.game.person.Hero;
import org.example.game.person.Unit;
import org.example.game.score.Record;

public class GameState implements Serializable {
    private static final long serialVersionUID = 1L;
    
    // Добавляем поля для отслеживания статистики
    private int enemiesDefeated = 0;
    private int castlesCaptured = 0;
    private int turnsCompleted = 0;
    private int unitsLost = 0;
    private String currentMapName;

    // Добавляем поля для хранения игровых сущностей
    private String playerName;
    private Player player;
    private GameMap gameMap;
    private Hero hero;
    private Enemy enemy;
    private HeroCastle heroCastle;
    private EnemyCastle enemyCastle;
    private List<Unit> allUnits;
    private Carriage carriage;
    private Road road;

    public GameState(String playerName, Player player, GameMap gameMap, Hero hero, Enemy enemy,
                     HeroCastle heroCastle, EnemyCastle enemyCastle, List<Unit> allUnits,
                     Carriage carriage, Road road) {
        this.playerName = playerName;
        this.player = player;
        this.gameMap = gameMap;
        this.hero = hero;
        this.enemy = enemy;
        this.heroCastle = heroCastle;
        this.enemyCastle = enemyCastle;
        this.allUnits = allUnits;
        this.carriage = carriage;
        this.road = road;
    }

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

    // Геттеры для игровых сущностей
    public String getPlayerName() {
        return playerName;
    }

    public Player getPlayer() {
        return player;
    }

    public GameMap getGameMap() {
        return gameMap;
    }

    public Hero getHero() {
        return hero;
    }

    public Enemy getEnemy() {
        return enemy;
    }

    public HeroCastle getHeroCastle() {
        return heroCastle;
    }

    public EnemyCastle getEnemyCastle() {
        return enemyCastle;
    }

    public List<Unit> getAllUnits() {
        return allUnits;
    }

    public Carriage getCarriage() {
        return carriage;
    }

    public Road getRoad() {
        return road;
    }
} 