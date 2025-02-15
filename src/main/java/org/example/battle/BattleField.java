package org.example.battle;

import org.example.entities.Hero;
import org.example.entities.Position;
import org.example.game.Cell;
import org.example.game.CellType;  // Импортируем CellType

public class BattleField {
    private final int width;
    private final int height;
    private final Cell[][] field;
    private final Hero playerHero;
    private final Hero enemyHero;

    public BattleField(int width, int height, Hero playerHero, Hero enemyHero) {
        this.width = width;
        this.height = height;
        this.field = new Cell[height][width];
        this.playerHero = playerHero;
        this.enemyHero = enemyHero;

        // Инициализация поля
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Заполняем поле пустыми клетками
                field[y][x] = new Cell(CellType.EMPTY, 0);
            }
        }

        // Размещение препятствий симметрично по диагонали
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Препятствие на главной диагонали и ее симметрии
                if (x == y || x == width - y - 1) {
                    field[y][x] = new Cell(CellType.OBSTACLE, 0);
                }
            }
        }

        // Убедимся, что препятствия не блокируют всю карту
        ensurePassages();

        // Размещение героев на поле сражения
        this.playerHero.setPosition(new Position(0, height / 2));
        this.enemyHero.setPosition(new Position(width - 1, height / 2));
    }

    // Метод, обеспечивающий проходы через поле с препятствиями
    private void ensurePassages() {
        // Создаем проход через центральную часть поля сражения
        int centerY = height / 2;
        for (int x = width / 4; x < 3 * width / 4; x++) {
            field[centerY][x] = new Cell(CellType.EMPTY, 0);
        }
    }

    public void printField() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                char displayChar = '.';
                if (field[y][x].getType() == CellType.OBSTACLE) {
                    displayChar = '#';
                } else if (field[y][x].getType() == CellType.EMPTY) {
                    displayChar = ' ';
                }
                System.out.print(displayChar + " ");
            }
            System.out.println();
        }
    }

    // Метод для проверки доступности клетки
    public boolean isWalkable(Position position) {
        return field[position.getY()][position.getX()].getType() != CellType.OBSTACLE;
    }
}