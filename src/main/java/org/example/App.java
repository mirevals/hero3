package org.example;

import org.example.game.Player;
import org.example.game.build.Castle;
import org.example.game.build.CastleManager;
import org.example.game.build.EnemyCastle;
import org.example.game.build.HeroCastle;
import org.example.game.map.GameMap;
import org.example.game.map.MapManager;
import org.example.game.person.Enemy;
import org.example.game.person.Hero;
import org.example.game.person.Team;

public class App {
    public static void main(String[] args) {


        GameMap gameMap = new GameMap(10, 10);


        // Создаем игрока
        Player player = new Player(1000);

        Hero hero = new Hero("Герой 1", 15, Team.HERO, 1000, gameMap.getWidth(), gameMap.getHeight(), 100, 100, 100, 1);

        Enemy enemy = new Enemy("Враг", 5, Team.ENEMY, 100, gameMap.getWidth(), gameMap.getHeight(), 100, 100, 100, 1);

        HeroCastle heroCastle = new HeroCastle(gameMap.getHeight(), gameMap.getWidth());

        EnemyCastle enemyCastle = new EnemyCastle(gameMap.getHeight(), gameMap.getWidth());

        MapManager mapManager = new MapManager(heroCastle, enemyCastle, enemy, hero, gameMap);

        CastleManager.enterCastle(heroCastle, hero, player, enemy, enemyCastle, heroCastle, gameMap, mapManager);
    }
}