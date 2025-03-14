package org.example.game.person;

import org.example.game.map.Position;
import org.example.game.build.HeroCastle;

import java.util.List;

public class Hero extends Character {



    public Hero(String name, int maxMoves, Team team, int gold) {
        super(name, maxMoves, HeroCastle.getPosition(), team, gold);
    }

    // Дополнительные методы или переопределения для класса Hero можно добавить здесь, если требуется
}