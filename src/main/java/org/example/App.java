package org.example;

import org.example.game.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class App {
    public static void main(String[] args) {


        Position heroStartPosition = new Position(MapManager.getHeroX(), MapManager.getHeroY());
        List<Unit> heroUnits = new ArrayList<>();
        heroUnits.add(new Unit(Unit.UnitType.WARRIOR, 100, 10, 5, 3, Team.HERO, heroStartPosition));
        Hero hero = new Hero("Герой", 5, heroStartPosition, heroUnits, Team.HERO);


        Position enemyStartPosition = new Position(MapManager.getHeroX(), MapManager.getHeroY());
        List<Unit> enemyUnits = new ArrayList<>();
        heroUnits.add(new Unit(Unit.UnitType.WARRIOR, 100, 10, 5, 3, Team.ENEMY, heroStartPosition));
        Enemy enemy = new Enemy("Враг", 5, enemyStartPosition, enemyUnits, Team.ENEMY);


        GameMap gameMap = new GameMap(10, 10, hero, enemy);
        gameMap.startGame();
    }
}