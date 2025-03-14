package org.example.game.person;

import org.example.game.Gold;
import org.example.game.build.EnemyCastle;
import org.example.game.map.Position;

import java.util.List;

public class Enemy extends Character {


    private int enemyX;
    private int enemyY;

    public Enemy(String name, int maxMoves, Team team, int gold) {
        super(name, maxMoves, EnemyCastle.getPosition(), team, gold);

        Position initialPosition = getEnemyInitialPosition();
        this.enemyX = initialPosition.getX();
        this.enemyY = initialPosition.getY();
    }

    public static Position getEnemyInitialPosition() {
        Position castlePosition = EnemyCastle.getPosition();
        if (castlePosition == null) {
            System.err.println("Warning: EnemyCastle position is null. Using default (0,0).");
            return new Position(0, 0);  // Значение по умолчанию, чтобы избежать ошибки
        }
        // Логика для определения позиции противника на карте
        int enemyX = 5 * EnemyCastle.getPosition().getX() / 6;
        int enemyY = EnemyCastle.getPosition().getY() / 4;
        return new Position(enemyX, enemyY);
    }

    public void setEnemyPosition(int x, int y) {
        this.enemyX = x;
        this.enemyY = y;
    }
}