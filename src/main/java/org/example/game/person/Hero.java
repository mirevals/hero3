package org.example.game.person;

import org.example.game.map.Position;

import java.util.List;

public class Hero extends Character {



    public Hero(String name, int maxMoves, Position startPosition, List<Unit> units, Team team, int gold) {
        super(name, maxMoves, startPosition, units, team, gold);
    }

    // Дополнительные методы или переопределения для класса Hero можно добавить здесь, если требуется
}