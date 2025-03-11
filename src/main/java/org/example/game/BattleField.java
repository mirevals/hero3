package org.example.game;



import java.util.ArrayList;
import java.util.List;

public class BattleField {

    private int width; // Ширина поля
    private int height; // Высота поля
    private Cell[][] field; // Матрица клеток на поле сражения
    private List<Unit> units; // Список юнитов, находящихся на поле

    public BattleField(int width, int height) {
        this.width = width;
        this.height = height;
        this.field = new Cell[width][height];
        this.units = new ArrayList<>();
        generateField();
    }

    // Генерация поля с препятствиями
    private void generateField() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                // Примерное распределение препятствий
                if (Math.random() < 0.2) { // 20% вероятности для препятствия
                    field[i][j] = new Cell(true); // Ячейка с препятствием
                } else {
                    field[i][j] = new Cell(false); // Пустая ячейка
                }
            }
        }
    }

    // Метод для размещения юнита на поле
    public boolean placeUnit(Unit unit, int x, int y) {
        // Проверка, что клетка не занята препятствием
        if (x >= 0 && x < width && y >= 0 && y < height && !field[x][y].hasObstacle()) {
            Position newPosition = new Position(x, y);  // Создаем новый объект Position
            unit.setPosition(newPosition);  // Устанавливаем позицию юнита
            units.add(unit);
            return true;
        }
        return false; // Невозможно поставить юнита на эту клетку
    }

    // Метод для проверки, можно ли перемещаться в указанную клетку
    public boolean canMoveTo(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height && !field[x][y].hasObstacle();
    }

    // Метод для получения состояния клетки
    public Cell getCell(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            return field[x][y];
        }
        return null; // Если выход за пределы карты
    }

    // Метод для вывода состояния поля
    public void printField() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (field[i][j].hasObstacle()) {
                    System.out.print("X "); // Препятствие
                } else {
                    System.out.print("O "); // Пустая клетка
                }
            }
            System.out.println();
        }
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