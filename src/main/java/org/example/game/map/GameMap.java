package org.example.game.map;

import org.example.game.person.Character;
import org.example.game.person.Enemy;
import org.example.game.person.Hero;
import org.example.game.battle.Battle;
import org.example.game.battle.BattleField;
import org.example.game.build.CastleManager;

import java.util.Scanner;

public class GameMap {

    private final Scanner scanner;

    private int width;
    private int height;
    public GameMap(int width, int height) {
        this.scanner = new Scanner(System.in);
        this.width = width;
        this.height = height;

    }



    Enemy enemy = CastleManager.getEnemy();






    public int getWidth(){
        return this.width;
    }

    public int getHeight(){
        return this.height;
    }
}