package org.example;

import org.example.game.Player;
import org.example.game.battle.Battle;
import org.example.game.battle.BattleField;
import org.example.game.build.*;
import org.example.game.map.GameMap;
import org.example.game.map.MapManager;
import org.example.game.map.Position;
import org.example.game.map.Road;
import org.example.game.person.*;

import java.util.ArrayList;
import java.util.List;

import static org.example.game.build.Shop.availableBuildings;


public class App {
    public static void main(String[] args) {


        GameMap gameMap = new GameMap(10, 10);


        // Создаем игрока
        Player player = new Player(1000);


        Unit warrior = new Unit(Unit.UnitType.WARRIOR, 100, 100, 1, 10, Team.HERO, 'W', 100);
        List<Unit> unitsHero = new ArrayList<>();


        // Создаем юнита и добавляем его в список юнитов врага
        Unit enemyUnit = new Unit(Unit.UnitType.WARRIOR, 100, 100, 1, 10, Team.ENEMY, 'A', 100);
        List<Unit> unitsEnemy = new ArrayList<>();
        unitsEnemy.add(enemyUnit);


        List<Unit> buyUnit = new ArrayList<>();
        buyUnit.add(warrior);

        unitsHero.add(warrior);

        Carriage carriage = new Carriage(new Position(5, 0), 1, 10, Carriage.Direction.DOWN);



        Hero hero = new Hero("Герой 1", 10, Team.HERO, 1000, gameMap.getWidth(), gameMap.getHeight(), 100, 100, 100, 3,  unitsHero);

        Enemy enemy = new Enemy("Враг", 5, Team.ENEMY, 100, gameMap.getWidth(), gameMap.getHeight(), 100, 100, 100, 1,  unitsEnemy);

        HeroCastle heroCastle = new HeroCastle(gameMap.getHeight(), gameMap.getWidth());

        EnemyCastle enemyCastle = new EnemyCastle(gameMap.getHeight(), gameMap.getWidth());

        Building building1 = availableBuildings.get(0);
        Building building2 = availableBuildings.get(1);

        enemyCastle.addBuilding(building1);
        enemyCastle.addBuilding(building2);



        List<Unit> allUnits = new ArrayList<>();
        allUnits.addAll(unitsHero);
        allUnits.addAll(unitsEnemy);


        BattleField battleField = new BattleField(allUnits);

        Road road = new Road(gameMap.getWidth() / 6, gameMap.getHeight() / 4, 5 * gameMap.getWidth() / 6, gameMap.getHeight() / 4);

        MapManager mapManager = new MapManager(heroCastle, enemyCastle, enemy, hero, gameMap, road, carriage);

        CastleManager.enterCastle(heroCastle, hero, player,
                enemy, enemyCastle, heroCastle, gameMap,
                mapManager, buyUnit, hero, battleField,
                allUnits, carriage);


        //Battle.autoFight(battleField, allUnits);

    }
}