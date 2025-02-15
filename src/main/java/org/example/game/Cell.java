package org.example.game;

public class Cell {
    private final CellType type;
    private final int cost;

    public Cell(CellType type, int cost) {
        this.type = type;
        this.cost = cost;
    }

    public CellType getType() {
        return type;
    }

    public int getCost() {
        return cost;
    }
}