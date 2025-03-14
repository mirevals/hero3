package org.example.game.person;

import org.example.game.Gold;
import org.example.game.build.EnemyCastle;
import org.example.game.map.Position;

import java.util.List;

public class Enemy extends Character {

    public Enemy(String name, int maxMoves, Team team, int gold) {
        super(name, maxMoves, EnemyCastle.getPosition(), team, gold);
    }

    public static Position getEnemyInitialPosition() {
        // Логика для определения позиции противника на карте
        int enemyX = 5 * EnemyCastle.getPosition().getX() / 6;
        int enemyY = EnemyCastle.getPosition().getY() / 4;
        return new Position(enemyX, enemyY);
    }
}