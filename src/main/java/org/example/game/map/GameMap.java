package org.example.game.map;

import java.io.Serializable;

public class GameMap implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int width;
    private final int height;
    private final char[][] map;

    // Конструктор карты
    public GameMap(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Размеры карты должны быть положительными числами");
        }
        this.width = width;
        this.height = height;
        this.map = new char[height][width];  // Инициализация карты с заданными размерами
        initializeMap();  // Инициализация карты с пустыми клетками
    }

    // Метод для получения ширины карты
    public int getWidth() {
        return this.width;
    }

    // Метод для получения высоты карты
    public int getHeight() {
        return this.height;
    }

    // Метод для получения карты
    public char[][] getMap() {
        return this.map;
    }

    // Метод для инициализации карты (по умолчанию заполняется пустыми клетками '.')
    private void initializeMap() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                map[i][j] = ' ';
            }
        }
    }

    // Метод для проверки валидности позиции на карте
    public boolean isValidPosition(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    // Метод для проверки валидности типа местности
    public boolean isValidTerrain(char terrain) {
        return terrain == 'C' || // Замок
               terrain == 'R' || // Дорога
               terrain == '#' || // Препятствие
               terrain == 'P' || // Равнина
               terrain == 'F' || // Лес
               terrain == 'M' || // Горы
               terrain == 'W';   // Вода
    }

    // Метод для получения значения клетки
    public char getCell(int x, int y) {
        if (!isValidPosition(x, y)) {
            throw new IndexOutOfBoundsException("Координаты выходят за пределы карты");
        }
        return map[y][x];
    }

    // Метод для установки значения клетки
    public void setCell(int x, int y, char value) {
        if (!isValidPosition(x, y)) {
            throw new IndexOutOfBoundsException("Координаты выходят за пределы карты");
        }
        map[y][x] = value;
    }

    // Метод для очистки карты
    public void clear() {
        initializeMap();
    }

    // Дополнительный метод для печати карты (для отладки)
    public void printMap() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                System.out.print(map[i][j] + " ");
            }
            System.out.println();
        }
    }

    public void setCellValue(int x, int y, char value) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            throw new IllegalArgumentException("Координаты выходят за пределы карты");
        }
        map[y][x] = value;
    }

    public char getCellValue(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            throw new IllegalArgumentException("Координаты выходят за пределы карты");
        }
        return map[y][x];
    }
}