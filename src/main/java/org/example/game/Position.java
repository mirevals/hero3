package org.example.game;

public class Position {

    private final int x;  // Координата X
    private final int y;  // Координата Y

    // Конструктор для инициализации координат
    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // Геттеры для получения координат
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    // Метод для получения смещения (если нужно)
    public int getOffset() {
        return 0;  // Можно определить логику для смещения, если требуется
    }
}