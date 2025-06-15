package org.example.game.save;

import org.example.game.Player;
import org.example.game.Virus;
import org.example.game.map.GameMap;
import org.example.game.map.Road;
import org.example.game.person.*;
import org.example.game.battle.BattleField;
import org.example.game.build.*;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

public class GameState implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String playerName;
    private final Player player;
    private final GameMap gameMap;
    private final Hero hero;
    private final Enemy enemy;
    private final HeroCastle heroCastle;
    private final EnemyCastle enemyCastle;
    private final List<Unit> allUnits;
    private final Carriage carriage;
    private final Road road;
    private final boolean accountInfected;
    private final List<Virus> accountViruses;
    private final long gameTimeInMinutes;

    public GameState(String playerName, Player player, GameMap gameMap, Hero hero, Enemy enemy,
                     HeroCastle heroCastle, EnemyCastle enemyCastle, List<Unit> allUnits,
                     Carriage carriage, Road road, boolean accountInfected, List<Virus> accountViruses) {
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
        this.accountInfected = accountInfected;
        this.accountViruses = new ArrayList<>(accountViruses);
        this.gameTimeInMinutes = 0;
        
        System.out.println("Creating GameState for " + playerName);
        System.out.println("Account infected: " + accountInfected);
        System.out.println("Number of viruses: " + accountViruses.size());
        for (Virus virus : accountViruses) {
            System.out.println("  - Virus: " + virus.getName() + " (ID: " + virus.getId() + ")");
        }
    }

    public GameState(String playerName, Player player, GameMap gameMap, Hero hero, Enemy enemy,
                    HeroCastle heroCastle, EnemyCastle enemyCastle, List<Unit> allUnits,
                    Carriage carriage, Road road, boolean accountInfected, List<Virus> accountViruses,
                    long gameTimeInMinutes) {
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
        this.accountInfected = accountInfected;
        this.accountViruses = new ArrayList<>(accountViruses);
        this.gameTimeInMinutes = gameTimeInMinutes;
        
        System.out.println("Creating GameState for " + playerName);
        System.out.println("Account infected: " + accountInfected);
        System.out.println("Number of viruses: " + accountViruses.size());
        for (Virus virus : accountViruses) {
            System.out.println("  - Virus: " + virus.getName() + " (ID: " + virus.getId() + ")");
        }
    }

    // Геттеры для всех полей
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

    public boolean isAccountInfected() {
        return accountInfected;
    }

    public List<Virus> getAccountViruses() {
        return new ArrayList<>(accountViruses);
    }

    public long getGameTimeInMinutes() {
        return gameTimeInMinutes;
    }
} 