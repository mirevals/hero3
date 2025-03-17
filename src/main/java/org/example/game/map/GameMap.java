package org.example.game.map;

import org.example.game.person.Character;
import org.example.game.person.Enemy;
import org.example.game.person.Hero;
import org.example.game.battle.Battle;
import org.example.game.battle.BattleField;
import org.example.game.build.CastleManager;

import java.util.Scanner;

public class GameMap {

    private final int width;
    private final int height;
    public GameMap(int width, int height) {
        this.width = width;
        this.height = height;

    }
    public int getWidth(){
        return this.width;
    }

    public int getHeight(){
        return this.height;
    }
}