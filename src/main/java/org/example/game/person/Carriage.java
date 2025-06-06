package org.example.game.person;

import org.example.game.map.Position;
import org.example.game.person.Character;
import java.io.Serializable;

public class Carriage implements Serializable {
    private static final long serialVersionUID = 1L;

    private Position position;
    private int speed; // Количество клеток, на которое перемещается за ход
    private int damage; // Урон, который наносит при столкновении
    private Direction direction;

    public enum Direction implements Serializable {
        LEFT, RIGHT, UP, DOWN
    }

    public Carriage(Position startPosition, int speed, int damage, Direction direction) {
        this.position = startPosition;
        this.speed = speed;
        this.damage = damage;
        this.direction = direction;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(int x, int y) {
        this.position = new Position(x, y);
    }

    public void setDirection(Direction direction){
        this.direction = direction;
    }

    public Direction getDirection() {
        return direction;
    }

    public int getSpeed() {
        return speed;
    }

    public void checkCollision(Character character) {
        if (character.getPosition().equals(position)) {
            character.setHealth(character.getHealth() - damage);
            System.out.println("Карета столкнулась с " + character.getName() + " и нанесла " + damage + " урона!");
            if (character.isDead()) {
                System.out.println(character.getName() + " погиб от столкновения с каретой!");
            }
        }
    }
}