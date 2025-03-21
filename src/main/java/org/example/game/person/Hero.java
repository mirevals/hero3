package org.example.game.person;

import org.example.game.map.Position;

import java.util.List;

public class Hero extends Character {

    public Hero(String name, int maxMoves, Team team, int gold, int width, int height,
                int health, int attack, int defense, int attackRange, List<Unit> units) {
        super(name, maxMoves, new Position(width / 6, height / 4), team, gold, health, attack, defense, attackRange, units);
    }

    @Override
    public CharacterType getType() {
        return CharacterType.HERO;
    }
}