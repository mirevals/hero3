package org.example.game;

public class GameMap {

    private final MapManager mapManager;

    public GameMap(int width, int height) {
        mapManager = new MapManager(width, height);
    }

    public boolean moveHero(int dx, int dy, Character character, Enemy enemy) {
        // Логика перемещения героя
        if (character.getCurrentMoves() <= 0) {
            return false;  // Герой не может перемещаться, если у него нет оставшихся перемещений
        }

        int heroX = mapManager.getHeroX();
        int heroY = mapManager.getHeroY();
        int newX = heroX + dx;
        int newY = heroY + dy;

        // Проверка на выход за пределы карты
        if (newX < 0 || newX >= mapManager.getWidth() || newY < 0 || newY >= mapManager.getHeight()) {
            return false;
        }

        // Проверка на проходимость клетки
        if (!mapManager.isWalkable(newX, newY)) {
            return false;
        }

        // Проверка, если мы движемся в замок (до перемещения)
        if (mapManager.getMap()[newY][newX] == 'L') {
            System.out.println("Вы подошли к замку! Вход возможен.");

            // Позволяем герою войти в замок
            CastleManager.enterCastle(character, this);
            return false;  // Прекращаем движение после входа в замок
        }

        // Если текущая клетка была занята героем, восстанавливаем её в исходное состояние
        if (mapManager.getMap()[heroY][heroX] == 'H') {
            mapManager.getMap()[heroY][heroX] = '.';  // Очищаем старое место героя
        }

        // Обновляем позицию героя
        mapManager.setHeroPosition(newX, newY);

        // Размещение героя на новой клетке
        mapManager.getMap()[newY][newX] = 'H';  // Размещение героя на новой клетке

        // Проверяем встречу с врагом
        if (newX == mapManager.getEnemyX() && newY == mapManager.getEnemyY()) {
            BattleField battleField = enterBattleField(character, enemy);
            battleField.autoFight();  // Начинаем бой
            return false;  // Возвращаем false, так как герой не может продолжить движение после боя
        }

        return true;  // Герой успешно переместился
    }

    public void exitCastle(Character character) {
        CastleManager.isInCastle = false;
        int heroX = mapManager.getHeroX();
        int heroY = mapManager.getHeroY();

        // Определяем, где был замок, а не где сейчас стоит герой
        int castleX = heroX;
        int castleY = heroY;

        // Ищем исходную клетку замка
        for (int y = 0; y < mapManager.getHeight(); y++) {
            for (int x = 0; x < mapManager.getWidth(); x++) {
                if (mapManager.getMap()[y][x] == 'L') {
                    castleX = x;
                    castleY = y;
                    break;
                }
            }
        }

        // Проверяем, что герой в замке
        if (mapManager.getMap()[heroY][heroX] == 'H') {
            // Перемещаем героя на клетку ниже замка
            mapManager.getMap()[heroY][heroX] = '.';  // Очищаем текущую позицию героя

            // Перемещаем героя на клетку ниже замка (если возможно)
            int newY = castleY + 1;
            if (newY < mapManager.getHeight() && mapManager.isWalkable(castleX, newY)) {
                mapManager.getMap()[newY][castleX] = 'H';  // Перемещаем героя вниз
                mapManager.setHeroPosition(castleX, newY);  // Обновляем координаты героя
                System.out.println("Вы покинули замок и переместились на клетку ниже.");
            } else {
                System.out.println("Вы не можете покинуть замок, нет свободной клетки ниже.");
            }

            // Восстанавливаем символ 'L' только в исходной позиции замка
            mapManager.getMap()[castleY][castleX] = 'L';

            // Отображаем обновленную карту
            printMap();
        }
    }

    public BattleField enterBattleField(Character hero, Enemy enemy) {
        // Создаем поле боя с юнитами героя и врага
        return new BattleField(hero.getUnits(), enemy.getUnits());
    }

    public void printMap() {
        // Печатаем карту
        mapManager.printMap();
    }

    // Дополнительные методы для получения позиций героя и врага
    public int getHeroX() {
        return mapManager.getHeroX();
    }

    public int getHeroY() {
        return mapManager.getHeroY();
    }

    public int getEnemyX() {
        return mapManager.getEnemyX();
    }

    public int getEnemyY() {
        return mapManager.getEnemyY();
    }

    // Дополнительно, можно добавить методы для перемещения врага или других элементов, если нужно
    public void moveEnemy(int dx, int dy) {
        // Логика перемещения врага, если требуется
        mapManager.moveEnemy(dx, dy);
    }
}