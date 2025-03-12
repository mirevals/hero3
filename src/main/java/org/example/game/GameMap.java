package org.example.game;

import java.util.Scanner;

public class GameMap {

    private final MapManager mapManager;
    private final Hero hero;
    private final Enemy enemy;
    private final Scanner scanner;

    public GameMap(int width, int height, Hero hero, Enemy enemy) {
        this.mapManager = new MapManager(width, height);
        this.hero = hero;
        this.enemy = enemy;
        this.scanner = new Scanner(System.in);
    }

    public void startGame() {
        printMap();

        while (true) {
            System.out.println("\nКоманды: ");
            System.out.println("w - вверх");
            System.out.println("s - вниз");
            System.out.println("a - влево");
            System.out.println("d - вправо");
            System.out.println("q - выход");

            System.out.print("Введите команду: ");
            String command = scanner.nextLine();

            switch (command) {
                case "w": moveHero(0, -1, hero, enemy); break;
                case "s": moveHero(0, 1, hero, enemy); break;
                case "a": moveHero(-1, 0, hero, enemy); break;
                case "d": moveHero(1, 0, hero, enemy); break;
                case "q":
                    System.out.println("Выход из игры.");
                    scanner.close();
                    return;
                default:
                    System.out.println("Неверная команда. Попробуйте снова.");
                    continue;
            }

            printMap();
        }
    }

    public boolean moveHero(int dx, int dy, Character character, Enemy enemy) {
        if (!canMove(character)) {
            return false;
        }

        int newX = mapManager.getHeroX() + dx;
        int newY = mapManager.getHeroY() + dy;

        if (!isValidMove(newX, newY)) {
            return false;
        }

        if (handleCastleEntry(character, newX, newY)) {
            return false;
        }

        updateHeroPosition(newX, newY);

        return !checkForBattle(character, enemy, newX, newY);
    }

    private boolean canMove(Character character) {
        return character.getCurrentMoves() > 0;
    }

    private boolean isValidMove(int x, int y) {
        return x >= 0 && x < mapManager.getWidth() && y >= 0 && y < mapManager.getHeight() && mapManager.isWalkable(x, y);
    }

    private boolean handleCastleEntry(Character character, int x, int y) {
        if (mapManager.getMap()[y][x] == 'L') {
            System.out.println("Вы подошли к замку! Вход возможен.");
            CastleManager.enterCastle(character, this);
            return true;
        }
        return false;
    }

    private void updateHeroPosition(int x, int y) {
        int oldX = mapManager.getHeroX();
        int oldY = mapManager.getHeroY();

        if (mapManager.getMap()[oldY][oldX] == 'H') {
            mapManager.getMap()[oldY][oldX] = '.';
        }

        mapManager.setHeroPosition(x, y);
        mapManager.getMap()[y][x] = 'H';
    }

    private boolean checkForBattle(Character character, Enemy enemy, int x, int y) {
        if (x == mapManager.getEnemyX() && y == mapManager.getEnemyY()) {
            // Создаем поле боя с юнитами героя и врага
            BattleField battleField = new BattleField(character.getUnits(), enemy.getUnits());
            Battle battle = new Battle(battleField);

            // Запускаем бой
            boolean heroWon = battle.autoFight();

            if (!heroWon) {
                System.out.println("Герой проиграл битву!");
                // Можно добавить обработку поражения, например, завершение игры
            }

            return true;
        }
        return false;
    }

    public void exitCastle(Character character) {
        CastleManager.isInCastle = false;
        int[] castlePos = findCastlePosition();
        int castleX = castlePos[0];
        int castleY = castlePos[1];
        int heroX = mapManager.getHeroX();
        int heroY = mapManager.getHeroY();

        if (mapManager.getMap()[heroY][heroX] == 'H') {
            mapManager.getMap()[heroY][heroX] = '.';

            int newY = castleY + 1;
            if (newY < mapManager.getHeight() && mapManager.isWalkable(castleX, newY)) {
                mapManager.getMap()[newY][castleX] = 'H';
                mapManager.setHeroPosition(castleX, newY);
                System.out.println("Вы покинули замок и переместились на клетку ниже.");
            } else {
                System.out.println("Вы не можете покинуть замок, нет свободной клетки ниже.");
            }

            mapManager.getMap()[castleY][castleX] = 'L';
            printMap();
        }
    }

    private int[] findCastlePosition() {
        for (int y = 0; y < mapManager.getHeight(); y++) {
            for (int x = 0; x < mapManager.getWidth(); x++) {
                if (mapManager.getMap()[y][x] == 'L') {
                    return new int[]{x, y};
                }
            }
        }
        return new int[]{-1, -1};
    }

    public BattleField enterBattleField(Character hero, Enemy enemy) {
        return new BattleField(hero.getUnits(), enemy.getUnits());
    }

    public void printMap() {
        mapManager.printMap();
    }

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

    public void moveEnemy(int dx, int dy) {
        if (canMoveEnemy(dx, dy)) {
            mapManager.moveEnemy(dx, dy);
        }
    }

    private boolean canMoveEnemy(int dx, int dy) {
        int newX = mapManager.getEnemyX() + dx;
        int newY = mapManager.getEnemyY() + dy;
        return isValidMove(newX, newY);
    }
}