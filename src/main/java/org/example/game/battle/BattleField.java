package org.example.game.battle;

import org.example.game.map.Position;
import org.example.game.person.Unit;

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

        // Автоматическое размещение юнитов
        placeUnits(heroUnits, true);  // Размещение героев на поле
        placeUnits(enemyUnits, false);  // Размещение врагов на поле
    }

    private void generateField() {
        // Препятствия по краям и диагоналям
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                // Создаем препятствия по внешним границам и диагоналям
                if (i == 0 || i == width - 1 || j == 0 || j == height - 1 || i == j || i + j == width - 1) {
                    field[i][j] = new Cell(true); // Препятствие
                } else {
                    field[i][j] = new Cell(false); // Без препятствий
                }
            }
        }
    }

    private void placeUnits(List<Unit> unitsList, boolean isHero) {
        int startX = isHero ? 1 : width - 2; // Герои на левой стороне, враги на правой
        int startY = 1;

        for (int i = 0; i < unitsList.size(); i++) {
            Unit unit = unitsList.get(i);
            int x = startX + i;  // Расставляем юнитов по горизонтали
            int y = startY + i;  // Расставляем юнитов по вертикали

            // Находим свободную клетку, если не удалось разместить на предложенной позиции
            if (!placeUnit(unit, x, y)) {
                placeUnitAtAnyFreePosition(unit);
            }
        }
    }

    private void placeUnitAtAnyFreePosition(Unit unit) {
        // Перебираем все клетки поля, чтобы найти свободную
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (placeUnit(unit, i, j)) {
                    System.out.println(unit.getSymbol() + " размещен на позиции: (" + i + ", " + j + ")");
                    return;  // Как только нашли свободное место, выходим
                }
            }
        }
        System.out.println("Не удалось разместить " + unit.getSymbol() + " на поле.");
    }

    public boolean placeUnit(Unit unit, int x, int y) {
        if (isValidPosition(x, y) && !field[x][y].hasObstacle() && !isUnitAtPosition(x, y)) {
            unit.setPosition(new Position(x, y));
            units.add(unit);
            return true;
        }
        return false;
    }
    private boolean isUnitAtPosition(int x, int y) {
        // Проверка, есть ли уже юнит в данной клетке
        for (Unit unit : units) {
            if (unit.getPosition().getX() == x && unit.getPosition().getY() == y) {
                return true;
            }
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
                    fieldOutput.append(field[i][j].hasObstacle() ? "X " : "  ");
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