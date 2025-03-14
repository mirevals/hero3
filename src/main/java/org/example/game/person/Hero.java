package org.example.game.person;

import org.example.game.map.Position;
import org.example.game.build.HeroCastle;

import java.util.List;

public class Hero extends Character {



    public Hero(String name, int maxMoves, Team team, int gold) {
        super(name, maxMoves, HeroCastle.getPosition(), team, gold);
    }


    // Метод для получения начальной позиции героя
    public static Position getHeroInitialPosition() {
        // Логика для определения позиции героя на карте
        int heroX = HeroCastle.getPosition().getX() / 6;
        int heroY = HeroCastle.getPosition().getY() / 4;
        return new Position(heroX, heroY);
    }
}