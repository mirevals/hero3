package org.example.game.map;

public class GameMap {

    private final int width;
    private final int height;
    private final char[][] map;

    // Конструктор карты
    public GameMap(int width, int height) {
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
        // Проверяем, находятся ли координаты в пределах карты
        if (x >= 0 && x < width && y >= 0 && y < height) {
            map[y][x] = value;  // Устанавливаем символ в соответствующую клетку
        } else {
            System.out.println("Ошибка: Координаты выходят за пределы карты.");
        }
    }
}