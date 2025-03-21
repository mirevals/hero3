package org.example.game.battle;

import org.example.game.map.Position;
import org.example.game.person.Unit;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BattleField {
    private final int width;
    private final int height;
    private final char[][] field; // Поле теперь массив символов
    private List<Unit> units; // Список всех юнитов

    private Random random = new Random(); // Для случайного размещения

    public BattleField(List<Unit> allUnits) {
        this.width = Math.max(10, allUnits.size());
        this.height = Math.max(10, allUnits.size());

        this.field = new char[width][height];
        this.units = new ArrayList<>();

        generateField();

        // Автоматическое размещение юнитов
        placeAllUnits(allUnits);
    }

    private void generateField() {
        // Заполняем поле символами
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                // Препятствия и границы
                if ((i == 0 || i == width - 1 || j == 0 || j == height - 1 || i == j || i + j == width - 1)
                        && !(i == width / 2 && j == height / 2)
                        && !(i == width / 2 - 1 && j == height / 2)
                        && !(i == width / 2 && j == height / 2 - 1)
                        && !(i == width / 2 - 1 && j == height / 2 - 1)) {
                    field[i][j] = 'X'; // Препятствие
                } else {
                    field[i][j] = ' '; // Пустое место
                }
            }
        }
    }

    private void placeAllUnits(List<Unit> allUnits) {
        List<Position> availablePositions = getAvailablePositions();
        int placedUnits = 0;

        // Размещаем все юниты (героев и врагов) по очереди
        for (Unit unit : allUnits) {
            if (!availablePositions.isEmpty()) {
                Position position = availablePositions.remove(random.nextInt(availablePositions.size()));
                placeUnit(unit, position.getX(), position.getY());
                placedUnits++;
            }
        }

        System.out.println("Количество размещенных юнитов: " + placedUnits);
    }

    private List<Position> getAvailablePositions() {
        List<Position> positions = new ArrayList<>();
        for (int i = 1; i < height - 1; i++) {
            for (int j = 1; j < width - 1; j++) {
                if (field[i][j] == ' ' && !isUnitAtPosition(i, j)) {
                    positions.add(new Position(i, j));
                }
            }
        }
        return positions;
    }

    public boolean placeUnit(Unit unit, int x, int y) {
        if (isValidPosition(x, y) && !isUnitAtPosition(x, y)) {
            unit.setPosition(new Position(x, y));
            units.add(unit);
            // Логируем размещение юнита
            System.out.println(unit.getName() + " размещен на позиции (" + x + ", " + y + ")");
            field[x][y] = unit.getSymbol(); // Заполняем поле символом юнита
            return true;
        }
        return false;
    }

    private boolean isUnitAtPosition(int x, int y) {
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
        return isValidPosition(x, y) && field[x][y] == ' ';
    }

    // Метод для передвижения юнитов по очереди
    public void moveUnits(List<Unit> allUnits) {
        // Перемещаем всех юнитов по очереди
        for (Unit unit : allUnits) {
            moveUnit(unit);  // Перемещаем каждого юнита из списка
        }

        printField();  // После перемещения всех юнитов печатаем обновленное поле
    }

    // Метод для перемещения юнита
    private void moveUnit(Unit unit) {
        // Пример логики движения - для демонстрации перемещаем юнита случайно на соседнюю клетку
        Position position = unit.getPosition();
        int x = position.getX();
        int y = position.getY();

        // Пробуем переместить юнита в соседнюю клетку
        int newX = x + random.nextInt(3) - 1; // Случайное изменение X (от -1 до 1)
        int newY = y + random.nextInt(3) - 1; // Случайное изменение Y (от -1 до 1)

        if (canMoveTo(newX, newY)) {
            field[x][y] = ' '; // Очистить старую позицию
            unit.setPosition(new Position(newX, newY)); // Обновить позицию юнита
            field[newX][newY] = unit.getSymbol(); // Установить новый символ
            System.out.println(unit.getName() + " перемещен на позицию (" + newX + ", " + newY + ")");
        }
    }

    // Печать поля
    public void printField() {
        StringBuilder fieldOutput = new StringBuilder();

        // Проходим по всем ячейкам и выводим соответствующие символы
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                fieldOutput.append(field[i][j]).append(" ");
            }
            fieldOutput.append("\n");
        }

        // Логируем вывод состояния поля
        System.out.println("Текущее состояние поля:");
        System.out.println(fieldOutput);
    }
    public char getCell(int x, int y) {
        // Проверяем, является ли позиция валидной
        if (isValidPosition(x, y)) {
            return field[x][y]; // Возвращаем содержимое ячейки
        } else {
            return ' '; // Если позиция невалидна, возвращаем пустое пространство
        }
    }

    public void setFieldCell(int x, int y, char symbol) {
        if (isValidPosition(x, y)) {
            field[x][y] = symbol;  // Устанавливаем символ в указанную ячейку
            System.out.println("Ячейка (" + x + ", " + y + ") обновлена на символ '" + symbol + "'");
        } else {
            System.out.println("Невалидная позиция (" + x + ", " + y + "). Ячейка не обновлена.");
        }
    }
}