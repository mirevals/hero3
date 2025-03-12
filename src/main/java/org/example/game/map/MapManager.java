package org.example.game.map;

public class MapManager {
    private final int width;
    private final int height;
    private final char[][] map;

    // Координаты героя и врага
    private static int heroX;
    private static int heroY;
    private int enemyX;
    private int enemyY;

    public MapManager(int width, int height) {
        this.width = width;
        this.height = height;
        this.map = new char[height][width];
        initializeMap();
    }

    private void initializeMap() {
        // Заполняем карту символами для замков, препятствий и дорог
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                map[y][x] = ' ';  // Все клетки сначала пустые
            }
        }

        // Размещение препятствий и замков
        placeObstaclesAndCastles();
        placeRoads();
        initializeCharacterPositions();
    }

    private void placeObstaclesAndCastles() {
        for (int y = 0; y < height; y++) {
            map[y][width / 3] = '#';  // Препятствия
            map[y][2 * width / 3] = '#';  // Препятствия
        }

        map[height / 4][width / 6] = 'L';  // Замок игрока
        map[height / 4][5 * width / 6] = 'L';  // Замок противника
    }

    private void placeRoads() {
        int startX = width / 6;
        int startY = height / 4;
        int endX = 5 * width / 6;
        int endY = height / 4;

        // Размещение дороги по горизонтали (не перекрывая замки)
        for (int x = startX; x <= endX; x++) {
            if (map[startY][x] != 'L') {  // Если клетка не занята замком
                map[startY][x] = '.';  // Размещение дороги
            }
        }

        // Размещение дороги по вертикали (не перекрывая замки)
        for (int y = startY + 1; y < height; y++) {
            if (map[y][endX] != 'L') {  // Если клетка не занята замком
                map[y][endX] = '.';  // Размещение дороги
            }
        }
    }

    private void initializeCharacterPositions() {
        // Позиция героя
        heroX = width / 6 + 1;
        heroY = height / 4 + 1;
        map[heroY][heroX] = 'H';  // Размещение героя

        // Позиция противника
        enemyX = 5 * width / 6 - 1;
        enemyY = height / 4 + 1;
        map[enemyY][enemyX] = 'A';  // Размещение противника
    }

    public boolean isWalkable(int x, int y) {
        return map[y][x] != '#';  // Проверка на препятствие
    }

    public int getMovementPenalty(int x, int y) {
        if (map[y][x] == 'P') {
            return 1;  // Минимальный штраф в области игрока
        } else if (map[y][x] == 'E') {
            return 1;  // Минимальный штраф в области противника
        } else if (map[y][x] == ' ') {
            return 3;  // Наибольший штраф на нейтральной территории
        }
        return Integer.MAX_VALUE;  // Невозможность перемещения по препятствиям
    }

    public void printMap() {
        // Печать заголовков для территорий
        System.out.println("Геройская территория: Левый участок");
        System.out.println("Нейтральная территория: Центральный участок");
        System.out.println("Вражеская территория: Правый участок");

        // Печать карты
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                System.out.print(map[y][x] + " ");
            }
            System.out.println();
        }
    }

    public char[][] getMap() {
        return map;
    }

    // Добавляем два метода для решения проблемы с getWidth и getHeight
    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    // Геттеры для координат героя и врага
    public static int getHeroX() {
        return heroX;
    }

    public static int getHeroY() {
        return heroY;
    }

    public int getEnemyX() {
        return enemyX;
    }

    public int getEnemyY() {
        return enemyY;
    }

    // Устанавливаем позицию героя
    public void setHeroPosition(int x, int y) {
        this.heroX = x;
        this.heroY = y;
    }

    // Перемещение врага
    public void moveEnemy(int dx, int dy) {
        enemyX += dx;
        enemyY += dy;
    }
}