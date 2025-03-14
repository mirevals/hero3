package org.example.game.person;

import org.example.game.build.EnemyCastle;
import org.example.game.map.Position;
import org.example.game.build.HeroCastle;

import java.util.List;

public class Hero extends Character {


    private int heroX;
    private int heroY;



    public Hero(String name, int maxMoves, Team team, int gold) {
        super(name, maxMoves, HeroCastle.getPosition(), team, gold);

        Position initialPosition = getHeroInitialPosition();
        this.heroX = initialPosition.getX();
        this.heroY = initialPosition.getY();
    }


    // Метод для получения начальной позиции героя
    public Position getHeroInitialPosition() {
        Position castlePosition = HeroCastle.getPosition();


        int heroX = castlePosition.getX() / 6;
        int heroY = castlePosition.getY() / 4;
        return new Position(heroX, heroY);
    }

    // Устанавливаем позицию героя
    public void setHeroPosition(int x, int y) {
        this.heroX = x;
        this.heroY = y;
    }
}