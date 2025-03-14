package org.example;

import org.example.game.Gold;
import org.example.game.map.GameMap;
import org.example.game.map.MapManager;
import org.example.game.map.Position;
import org.example.game.person.Enemy;
import org.example.game.person.Hero;
import org.example.game.person.Team;
import org.example.game.person.Unit;

import java.util.ArrayList;
import java.util.List;

public class App {
    public static void main(String[] args) {


        Hero hero = new Hero("Герой", 15, Team.HERO, 1000);


        Enemy enemy = new Enemy("Враг", 5, Team.ENEMY, 0);


        GameMap gameMap = new GameMap(10, 10, hero, enemy);
        gameMap.startGame();
    }
}