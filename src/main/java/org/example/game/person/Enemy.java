package org.example.game.person;

import org.example.game.map.Position;

import java.util.List;

public class Enemy extends Character {

    public Enemy(String name, int maxMoves, Position startPosition, List<Unit> units, Team team) {
        super(name, maxMoves, startPosition, units, team);
    }

    // Дополнительные методы или переопределения для класса Enemy можно добавить здесь, если требуется
}