package org.example.game.person;

import org.example.game.Gold;
import org.example.game.build.EnemyCastle;
import org.example.game.map.Position;

import java.util.List;

public class Enemy extends Character {


    public Enemy(String name, int maxMoves, Team team, int gold, int width, int height,
                 int health, int attack, int defense, int attackRange, List<Unit> units) {
        super(name, maxMoves, new Position(5 * width / 6, height / 4), team, gold, health, attack, defense, attackRange, units);
    }

    @Override
    public CharacterType getType() {
        return CharacterType.ENEMY;
    }
}