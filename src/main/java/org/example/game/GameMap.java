package org.example.game;

public class GameMap {

    private final int width;
    private final int height;
    private final char[][] map;  // Карта будет представлять собой массив символов.
    private int heroX;  // Координата X героя
    private int heroY;  // Координата Y героя
    private int enemyX;  // Координата X врага
    private int enemyY;  // Координата Y врага

    // Конструктор для создания карты с размером n x m.
    public GameMap(int width, int height) {
        this.width = width;
        this.height = height;
        this.map = new char[height][width];

        // Инициализация карты
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Разделение карты на три области
                if (x < width / 3) {
                    map[y][x] = 'P';  // Область игрока
                } else if (x < 2 * width / 3) {
                    map[y][x] = '.';  // Нейтральная территория
                } else {
                    map[y][x] = 'E';  // Область противника
                }
            }
        }

        // Размещение препятствий на границе между областями
        for (int y = 0; y < height; y++) {
            map[y][width / 3] = '#';  // Препятствия между областью игрока и нейтральной
            map[y][2 * width / 3] = '#';  // Препятствия между нейтральной областью и противником
        }

        // Размещение замков
        // Замок игрока в области игрока (например, в верхнем левом углу области игрока)
        map[height / 4][width / 6] = 'L';  // Замок игрока

        // Замок противника в области противника (например, в верхнем правом углу области противника)
        map[height / 4][5 * width / 6] = 'L';  // Замок противника

        // Размещение дороги между замками игрока и противника
        // Прокладываем дорогу от замка игрока до замка противника
        int startX = width / 6;  // X координата замка игрока
        int startY = height / 4; // Y координата замка игрока
        int endX = 5 * width / 6;  // X координата замка противника
        int endY = height / 4;     // Y координата замка противника

        // Прокладываем дорогу между замками (по горизонтали и вертикали)
        for (int x = startX; x <= endX; x++) {
            map[startY][x] = 'R';  // Размещение дороги по горизонтали
        }
        for (int y = startY + 1; y < height / 2; y++) {
            map[y][endX] = 'R';  // Размещение дороги по вертикали, если нужно
        }

        // Размещение героя в области игрока (вблизи замка)
        heroX = width / 6;
        heroY = height / 4 + 1;  // Немного ниже замка
        map[heroY][heroX] = 'H';  // Размещение героя

        // Размещение врага в области противника (вблизи замка)
        enemyX = 5 * width / 6;
        enemyY = height / 4 + 1;  // Немного ниже замка
        map[enemyY][enemyX] = 'A';  // Размещение врага
    }

    // Геттеры для получения координат героя и врага
    public int getHeroX() {
        return heroX;
    }

    public int getHeroY() {
        return heroY;
    }

    public int getEnemyX() {
        return enemyX;
    }

    public int getEnemyY() {
        return enemyY;
    }

    // Метод для проверки, можно ли пройти по клетке (если это не препятствие).
    public boolean isWalkable(int x, int y) {
        return map[y][x] != '#';  // Проверка на препятствие
    }

    // Метод для получения штрафа на клетке
    public int getMovementPenalty(int x, int y) {
        if (map[y][x] == 'P') {
            return 1;  // Минимальный штраф в области игрока
        } else if (map[y][x] == 'E') {
            return 1;  // Минимальный штраф в области противника
        } else if (map[y][x] == '.') {
            return 3;  // Наибольший штраф на нейтральной территории
        } else {
            return Integer.MAX_VALUE;  // Невозможность перемещения по препятствиям
        }
    }

    public boolean moveHero(int dx, int dy, Hero hero) {
        // Проверяем, можно ли еще перемещаться (герой не использовал все свои перемещения)
        if (hero.getCurrentMoves() <= 0) {
            return false;  // Герой не может перемещаться, если у него нет оставшихся перемещений
        }

        // Получаем текущие координаты героя
        int heroX = hero.getPosition().getX();
        int heroY = hero.getPosition().getY();

        int newX = heroX + dx;
        int newY = heroY + dy;

        // Проверка на выход за пределы карты
        if (newX < 0 || newX >= width || newY < 0 || newY >= height) {
            return false;  // Герой не может выйти за пределы карты
        }

        // Проверка на проходимость клетки и штраф
        if (!isWalkable(newX, newY)) {
            return false;  // Герой не может пройти через препятствия
        }

        // Обновляем позицию героя на карте
        map[heroY][heroX] = '.';  // Освобождаем старую позицию
        map[newY][newX] = 'H';    // Обновляем позицию героя

        // Обновляем позицию героя в объекте Hero
        hero.move(new Position(newX, newY));  // Двигаем героя и уменьшаем количество перемещений

        return true;
    }
    // Метод для отображения карты в текстовом виде.
    public void printMap() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                System.out.print(map[y][x] + " ");
            }
            System.out.println();
        }
    }

    // Геттеры для получения размеров карты.
    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public char[][] getMap() {
        return map;
    }
}