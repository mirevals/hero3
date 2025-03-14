package org.example.game.map;

public class Road {
    private int startX;
    private int startY;
    private int endX;
    private int endY;

    public Road(int startX, int startY, int endX, int endY) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
    }

    // Метод для размещения дороги на карте
    public void placeRoad(char[][] map) {
        // Размещение дороги по горизонтали (не перекрывая замки)
        for (int x = startX; x <= endX; x++) {
            if (map[startY][x] != 'C' && map[startY][x] != 'E') {  // Если клетка не занята замками
                map[startY][x] = '.';  // Размещение дороги
            }
        }

        // Размещение дороги по вертикали (не перекрывая замки)
        for (int y = startY + 1; y < map.length; y++) {
            if (map[y][endX] != 'C' && map[y][endX] != 'E') {  // Если клетка не занята замками
                map[y][endX] = '.';  // Размещение дороги
            }
        }
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