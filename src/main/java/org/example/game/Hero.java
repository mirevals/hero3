package org.example.game;

import java.util.List;

public class Hero extends Character {

    public Hero(String name, int maxMoves, Position startPosition, List<Unit> units, Team team) {
        super(name, maxMoves, startPosition, units, team);
    }

    // Дополнительные методы или переопределения для класса Hero можно добавить здесь, если требуется
}