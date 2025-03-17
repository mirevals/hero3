package org.example.game.person;

import org.example.game.Gold;
import org.example.game.build.EnemyCastle;
import org.example.game.map.Position;

import java.util.List;

public class Enemy extends Character {


    public Enemy(String name, int maxMoves, Team team, int gold, int width, int height) {
        super(name, maxMoves, new Position(5 * width / 6, height / 4), team, gold);
    }

    public void setEnemyPosition(int x, int y) {
        this.position = new Position(x, y);
    }
}