package org.example.game.map;

import org.example.game.build.EnemyCastle;
import org.example.game.build.HeroCastle;
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

    private final HeroCastle playerCastle;
    private final EnemyCastle enemyCastle;



    public MapManager(int width, int height) {
        this.width = width;
        this.height = height;
        this.map = new char[height][width];

        this.playerCastle = new HeroCastle(height, width);
        this.enemyCastle = new EnemyCastle(height, width);

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
        Road road = new Road(width / 6, height / 4, 5 * width / 6, height / 4);
        road.placeRoad(map);
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
// Получаем позиции замков
        Position playerCastlePos = HeroCastle.getPosition();
        Position enemyCastlePos = EnemyCastle.getPosition();

        // Размещение замков
        map[playerCastlePos.getY()][playerCastlePos.getX()] = 'C';  // Замок игрока (C)
        map[enemyCastlePos.getY()][enemyCastlePos.getX()] = 'E';  // Замок противника (E)
    }





    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }


    private void initializeCharacterPositions() {
        // Позиция героя
        heroX = Hero.getHeroInitialPosition().getX();
        heroY = Hero.getHeroInitialPosition().getY();
        map[heroY][heroX] = 'H';  // Размещение героя

        // Позиция противника
        enemyX = Enemy.getEnemyInitialPosition().getX();
        enemyY = Enemy.getEnemyInitialPosition().getY();
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