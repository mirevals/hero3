package org.example.game.person;

import org.example.game.Gold;
import org.example.game.build.EnemyCastle;
import org.example.game.map.Position;

import java.util.List;

public class Enemy extends Character {

    public Enemy(String name, int maxMoves, Team team, int gold) {
        super(name, maxMoves, EnemyCastle.getPosition(), team, gold);
    }
}