package org.example.game;

import java.util.ArrayList;
import java.util.List;

public class BattleField {
    private int width;
    private int height;
    private Cell[][] field;
    private List<Unit> units;

    public BattleField(List<Unit> heroUnits, List<Unit> enemyUnits) {
        this.width = Math.max(10, heroUnits.size() + enemyUnits.size());
        this.height = Math.max(10, heroUnits.size() + enemyUnits.size());

        this.field = new Cell[width][height];
        this.units = new ArrayList<>();
        generateField();

        // Размещение юнитов на поле
        for (Unit unit : heroUnits) placeUnit(unit, unit.getPosition().getX(), unit.getPosition().getY());
        for (Unit unit : enemyUnits) placeUnit(unit, unit.getPosition().getX(), unit.getPosition().getY());
    }

    private void generateField() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                field[i][j] = new Cell(i == j || i + j == width - 1);
            }
        }
    }

    public boolean placeUnit(Unit unit, int x, int y) {
        if (isValidPosition(x, y) && !field[x][y].hasObstacle()) {
            unit.setPosition(new Position(x, y));
            units.add(unit);
            return true;
        }
        return false;
    }

    public boolean isValidPosition(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    public boolean canMoveTo(int x, int y) {
        return isValidPosition(x, y) && !field[x][y].hasObstacle();
    }

    public List<Unit> getUnits() {
        return units;
    }

    public void printField() {
        StringBuilder fieldOutput = new StringBuilder();

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                boolean isUnitPresent = false;
                for (Unit unit : units) {
                    if (unit.getPosition().getX() == i && unit.getPosition().getY() == j) {
                        fieldOutput.append(unit.getSymbol()).append(" ");
                        isUnitPresent = true;
                        break;
                    }
                }
                if (!isUnitPresent) {
                    fieldOutput.append(field[i][j].hasObstacle() ? "X " : "O ");
                }
            }
            fieldOutput.append("\n");
        }

        System.out.println(fieldOutput);
    }

    public static class Cell {
        private boolean hasObstacle;

        public Cell(boolean hasObstacle) {
            this.hasObstacle = hasObstacle;
        }

        public boolean hasObstacle() {
            return hasObstacle;
        }
    }
}