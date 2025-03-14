package org.example.game.map;

import org.example.game.person.Character;
import org.example.game.person.Enemy;
import org.example.game.person.Hero;

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
        // Левый участок - собственная территория
        for (int y = 0; y < height; y++) {
            map[y][width / 3] = '#';  // Препятствия
        }

        // Правый участок - территория противника
        for (int y = 0; y < height; y++) {
            map[y][2 * width / 3] = '#';  // Препятствия
        }

        // Размещение замков
        map[height / 4][width / 6] = 'C';  // Замок игрока (C)
        map[height / 4][5 * width / 6] = 'E';  // Замок противника (E)
    }
    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    private void placeRoads() {
        int startX = width / 6;
        int startY = height / 4;
        int endX = 5 * width / 6;
        int endY = height / 4;

        // Размещение дороги по горизонтали (не перекрывая замки)
        for (int x = startX; x <= endX; x++) {
            if (map[startY][x] != 'C' && map[startY][x] != 'E') {  // Если клетка не занята замками
                map[startY][x] = '.';  // Размещение дороги
            }
        }

        // Размещение дороги по вертикали (не перекрывая замки)
        for (int y = startY + 1; y < height; y++) {
            if (map[y][endX] != 'C' && map[y][endX] != 'E') {  // Если клетка не занята замками
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

    public void removeEnemy(Enemy enemy) {
        int enemyX = getEnemyX();
        int enemyY = getEnemyY();

        // Убираем символ врага с карты
        if (enemyX >= 0 && enemyY >= 0 && enemyX < width && enemyY < height) {
            map[enemyY][enemyX] = ' '; // Убираем врага
            System.out.println("Враг удален с позиции: (" + enemyX + ", " + enemyY + ")");
        }
    }

    public void setEnemyPosition(int x, int y) {
        this.enemyX = x;
        this.enemyY = y;
    }

    // Методы для получения позиций замков
    public int[] getPlayerCastlePosition() {
        return new int[] { width / 6, height / 4 };  // Позиция замка игрока
    }

    public int[] getEnemyCastlePosition() {
        return new int[] { 5 * width / 6, height / 4 };  // Позиция замка врага
    }

    // Методы для установки новых позиций замков
    public void setPlayerCastlePosition(int x, int y) {
        map[height / 4][width / 6] = ' ';  // Убираем старое местоположение
        map[y][x] = 'C';  // Устанавливаем новый замок игрока
    }

    public void setEnemyCastlePosition(int x, int y) {
        map[height / 4][5 * width / 6] = ' ';  // Убираем старое местоположение
        map[y][x] = 'E';  // Устанавливаем новый замок врага
    }

    // Вывод позиций замков
    public void printCastlePositions() {
        int[] playerCastle = getPlayerCastlePosition();
        int[] enemyCastle = getEnemyCastlePosition();

        System.out.println("Позиция замка игрока: X=" + playerCastle[0] + ", Y=" + playerCastle[1]);
        System.out.println("Позиция замка врага: X=" + enemyCastle[0] + ", Y=" + enemyCastle[1]);
    }

    public int getMovementPenalty(int x, int y) {
        char terrain = getMap()[y][x];  // Получаем тип текущей клетки

        // Геройская территория
        if (x < width / 3) {
            return 0;  // Нет штрафа на своей территории
        }
        // Если клетка - дорога, проверяем территорию
        if (terrain == '.') {
            return 1;  // Штраф на дороге, если не на своей территории
        }



        // Вражеская территория
        else if (x > 2 * width / 3) {
            return 1;  // Минимальный штраф на территории врага
        }

        // Нейтральная территория
        return 2;  // Штраф на нейтральной территории
    }

    // Новый метод для получения типа территории
    public String getTerritoryType(int x, int y) {
        // Геройская территория
        if (x < width / 3) {
            return "Геройская территория";
        }
        // Вражеская территория
        else if (x > 2 * width / 3) {
            return "Вражеская территория";
        }
        // Нейтральная территория
        return "Нейтральная территория";
    }
}