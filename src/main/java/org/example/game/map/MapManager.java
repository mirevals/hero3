package org.example.game.map;

import org.example.game.build.Castle;
import org.example.game.build.CastleManager;
import org.example.game.build.EnemyCastle;
import org.example.game.build.HeroCastle;
import org.example.game.person.Character;
import org.example.game.person.Enemy;
import org.example.game.person.Hero;

public class MapManager {
    private final int width;
    private final int height;
    private final char[][] map;
    private Hero selectedHero; // Переменная для выбранного героя



    private HeroCastle heroCastle;
    private EnemyCastle enemyCastle;


    // Координаты героя и врага
    private static int heroX;
    private static int heroY;
    private int enemyX;
    private int enemyY;


    public MapManager(int width, int height) {
        this.width = width;
        this.height = height;
        this.map = new char[height][width];

        // Создаем замки
        heroCastle = new HeroCastle(height, width);
        enemyCastle = new EnemyCastle(height, width);

        initializeMap();
    }

    private void initializeMap() {
        // Заполняем карту символами для замков, препятствий и дорог
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                map[y][x] = ' ';  // Все клетки сначала пустые
            }
        }

        // Размещение препятствий, замков, дорог
        Terrain.placeObstacles(map, width, height);
        placeCastles();
        Road road = new Road(width / 6, height / 4, 5 * width / 6, height / 4);
        road.placeRoad(map);
        initializeCharacterPositions();
    }

    private void placeCastles() {
        // Получаем позиции замков
        Position heroCastlePos = heroCastle.getPosition();
        Position enemyCastlePos = enemyCastle.getPosition();

        // Размещение замков на карте
        map[heroCastlePos.getY()][heroCastlePos.getX()] = 'C';  // Замок игрока
        map[enemyCastlePos.getY()][enemyCastlePos.getX()] = 'E';  // Замок противника
    }

    private void initializeCharacterPositions() {
        // Позиция героя
        heroX = width / 6;
        heroY = height / 4;
        map[heroY][heroX] = 'H';  // Размещение героя

        // Позиция противника
        enemyX = 5 * width / 6;
        enemyY = height / 4;
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

    public void removeEnemy(Enemy enemy) {
        int enemyX = getEnemyX();
        int enemyY = getEnemyY();

        // Убираем символ врага с карты
        if (enemyX >= 0 && enemyY >= 0 && enemyX < width && enemyY < height) {
            map[enemyY][enemyX] = ' '; // Убираем врага
            System.out.println("Враг удален с позиции: (" + enemyX + ", " + enemyY + ")");
        }
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

    public char[][] getMap() {
        return map;
    }

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

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

}