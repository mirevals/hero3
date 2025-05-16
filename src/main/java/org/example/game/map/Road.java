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

    public boolean isPointOnRoad(int x, int y) {
        // Для горизонтальной дороги
        if (startY == endY && y == startY) {
            int minX = Math.min(startX, endX);
            int maxX = Math.max(startX, endX);
            return x >= minX && x <= maxX;
        }
        
        // Для вертикальной дороги
        if (startX == endX && x == startX) {
            int minY = Math.min(startY, endY);
            int maxY = Math.max(startY, endY);
            return y >= minY && y <= maxY;
        }
        
        // Для диагональной дороги
        if (Math.abs(endX - startX) == Math.abs(endY - startY)) {
            int dx = endX > startX ? 1 : -1;
            int dy = endY > startY ? 1 : -1;
            int currentX = startX;
            int currentY = startY;
            
            while (currentX != endX + dx) {
                if (x == currentX && y == currentY) {
                    return true;
                }
                currentX += dx;
                currentY += dy;
            }
        }
        
        return false;
    }
}