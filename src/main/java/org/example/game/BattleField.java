package org.example.game;

import java.util.ArrayList;
import java.util.List;

public class BattleField {

    private int width; // Ширина поля
    private int height; // Высота поля
    private Cell[][] field; // Матрица клеток на поле сражения
    private List<Unit> units; // Список юнитов, находящихся на поле

    public BattleField(List<Unit> heroUnits, List<Unit> enemyUnits) {
        // Пример расчета размеров поля в зависимости от юнитов
        this.width = Math.max(10, heroUnits.size() + enemyUnits.size());  // Минимум 10 по ширине
        this.height = Math.max(10, heroUnits.size() + enemyUnits.size()); // Минимум 10 по высоте

        this.field = new Cell[width][height];
        this.units = new ArrayList<>();
        generateField();

        // Размещение юнитов
        for (Unit unit : heroUnits) {
            placeUnit(unit, unit.getPosition().getX(), unit.getPosition().getY());
        }
        for (Unit unit : enemyUnits) {
            placeUnit(unit, unit.getPosition().getX(), unit.getPosition().getY());
        }
    }

    // Генерация поля с симметричными препятствиями
    private void generateField() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (i == j || i + j == width - 1) {
                    field[i][j] = new Cell(true); // Препятствие
                } else {
                    field[i][j] = new Cell(false); // Пустая ячейка
                }
            }
        }
    }

    public boolean placeUnit(Unit unit, int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            // Проверяем, что клетка не занята препятствием
            if (!field[x][y].hasObstacle()) {
                Position newPosition = new Position(x, y);
                unit.setPosition(newPosition);
                units.add(unit);
                return true; // Юнит успешно размещен
            }
        }
        return false; // Не удалось разместить юнита
    }

    public Cell[][] getField() {
        return field;
    }

    // Метод для проверки, можно ли перемещаться в указанную клетку
    public boolean canMoveTo(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height && !field[x][y].hasObstacle();
    }

    // Метод для поиска ближайшего врага
    private Unit findNearestEnemy(Unit unit) {
        Unit nearestEnemy = null;
        double minDistance = Double.MAX_VALUE;

        for (Unit target : units) {
            if (target.getTeam() != unit.getTeam()) {
                double distance = Math.sqrt(Math.pow(target.getPosition().getX() - unit.getPosition().getX(), 2) +
                        Math.pow(target.getPosition().getY() - unit.getPosition().getY(), 2));
                if (distance < minDistance) {
                    minDistance = distance;
                    nearestEnemy = target;
                }
            }
        }

        return nearestEnemy;
    }

    // Метод для выполнения автобоя
    public void autoFight() {
        for (Unit unit : units) {
            if (unit.getHealth() > 0) {
                Unit enemy = findNearestEnemy(unit);

                if (enemy != null) {
                    double distance = Math.sqrt(Math.pow(enemy.getPosition().getX() - unit.getPosition().getX(), 2) +
                            Math.pow(enemy.getPosition().getY() - unit.getPosition().getY(), 2));

                    if (distance <= unit.getAttackRange()) {
                        unit.attack(enemy);
                    } else if (distance <= unit.getMoveRange()) {
                        moveUnitTowardsEnemy(unit, enemy);
                    }
                }
            }
        }

        // Выводим поле после каждого шага
        printField();
    }

    // Метод для перемещения юнита к врагу
    private void moveUnitTowardsEnemy(Unit unit, Unit enemy) {
        Position enemyPosition = enemy.getPosition();
        Position unitPosition = unit.getPosition();

        int dx = Integer.compare(enemyPosition.getX(), unitPosition.getX());
        int dy = Integer.compare(enemyPosition.getY(), unitPosition.getY());

        int newX = unitPosition.getX() + dx;
        int newY = unitPosition.getY() + dy;

        if (canMoveTo(newX, newY)) {
            unit.setPosition(new Position(newX, newY));
            System.out.println(unit.getName() + " перемещается к врагу.");
        }
    }

    // Метод для вывода состояния поля, включая юнитов
    public void printField() {
        StringBuilder fieldOutput = new StringBuilder();

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                boolean isUnitPresent = false;
                for (Unit unit : units) {
                    Position unitPosition = unit.getPosition();
                    if (unitPosition.getX() == i && unitPosition.getY() == j) {
                        fieldOutput.append(unit.getSymbol()).append(" "); // Отображаем символ юнита
                        isUnitPresent = true;
                        break;
                    }
                }
                if (!isUnitPresent) {
                    if (field[i][j].hasObstacle()) {
                        fieldOutput.append("X "); // Препятствие
                    } else {
                        fieldOutput.append("O "); // Пустая клетка
                    }
                }
            }
            fieldOutput.append("\n");
        }

        System.out.println(fieldOutput.toString());
    }

    // Класс для представления клетки на поле
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