package org.example.game;

public enum TerrainType {
    GRASS('.', 1),
    ROAD('=', 0),
    WATER('~', 99),
    MOUNTAIN('^', 99),
    FOREST('*', 2);

    private final char symbol;
    private final int movementPenalty;

    TerrainType(char symbol, int movementPenalty) {
        this.symbol = symbol;
        this.movementPenalty = movementPenalty;
    }

    public char getSymbol() {
        return symbol;
    }

    public int getMovementPenalty() {
        return movementPenalty;
    }
}