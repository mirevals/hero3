package org.example.entities;

public class Gold {
    private final int amount;
    private final Position position;

    public Gold(int amount, Position position) {
        this.amount = amount;
        this.position = position;
    }

    public int getAmount() {
        return amount;
    }

    public Position getPosition() {
        return position;
    }
}