package org.example.game.map;

import java.io.Serializable;

public class Road implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private final int startX;
    private final int startY;
    private final int endX;
    private final int endY;

    public Road(int startX, int startY, int endX, int endY) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
    }

    // Метод для размещения дороги на карте
    public void placeRoad(char[][] map) {
        // Размещаем дорогу на карте
        int dx = Integer.compare(endX, startX);
        int dy = Integer.compare(endY, startY);

        int x = startX;
        int y = startY;

        // Сначала идем по X
        while (x != endX) {
            map[y][x] = '.';
            x += dx;
        }

        // Затем по Y
        while (y != endY) {
            map[y][x] = '.';
            y += dy;
        }

        // Ставим точку в конечной позиции
        map[endY][endX] = '.';
    }

    // Геттеры для начала и конца дороги
    public int getStartX() {
        return startX;
    }

    public int getStartY() {
        return startY;
    }

    public int getEndX() {
        return endX;
    }

    public int getEndY() {
        return endY;
    }
}