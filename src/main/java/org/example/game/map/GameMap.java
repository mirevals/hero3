package org.example.game.map;

import org.example.game.person.Character;
import org.example.game.person.Enemy;
import org.example.game.person.Hero;
import org.example.game.battle.Battle;
import org.example.game.battle.BattleField;
import org.example.game.build.CastleManager;

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
        // Проверка на наличие оставшихся шагов
        if (character.getCurrentMoves() <= 0) {
            System.out.println("У героя не осталось шагов.");
            return false;  // Если шагов нет, не даем двигаться
        }

        // Проверка на возможность движения (например, не блокирует ли что-то)
        if (!canMove(character)) {
            return false;
        }

        // Рассчитываем новое положение
        int newX = mapManager.getHeroX() + dx;
        int newY = mapManager.getHeroY() + dy;

        // Проверка на допустимость перемещения
        if (!isValidMove(newX, newY)) {
            return false;
        }

        // Обработка входа в замок
        if (handleCastleEntry(character, newX, newY)) {
            return false;
        }

        // Информация о типе территории
        String terrainType = mapManager.getTerritoryType(newX, newY);
        System.out.println("Герой шагает на территорию: " + terrainType);

        // Вычисление штрафа за территорию
        int penalty = mapManager.getMovementPenalty(newX, newY);
        character.setCurrentMoves(character.getCurrentMoves() - penalty);

        // Проверка, хватит ли шагов для этого перемещения
        if (character.getCurrentMoves() < 0) {
            System.out.println("Недостаточно шагов для перемещения на эту территорию.");
            return false;  // Если не хватает шагов, не даем двигаться
        }

        // Обновляем количество оставшихся шагов
        character.setCurrentMoves(character.getCurrentMoves());

        // Обновляем позицию героя
        updateHeroPosition(newX, newY);

        // Вывод оставшихся шагов
        System.out.println("Оставшиеся шаги: " + character.getCurrentMoves());

        // Проверка на бой
        return !checkForBattle(character, enemy, newX, newY);
    }


    private boolean canMove(Character character) {
        return character.getCurrentMoves() > 0; // Проверка на количество оставшихся шагов
    }

    private boolean isValidMove(int x, int y) {
        return x >= 0 && x < mapManager.getWidth() && y >= 0 && y < mapManager.getHeight() && mapManager.isWalkable(x, y);
    }

    private boolean handleCastleEntry(Character character, int x, int y) {
        char castleType = mapManager.getMap()[y][x];

        if (castleType == 'C') {
            // Вход в замок героя
            System.out.println("Вы подошли к замку героя! Вход возможен.");
            CastleManager.enterCastle(character, this, 'C'); // Входим в замок героя
            return true;
        } else if (castleType == 'E') {
            // Вход в замок противника
            System.out.println("Вы подошли к замку противника! Вход невозможен.");
            CastleManager.enterCastle(character, this, 'E'); // Входим в замок героя

            return true;
        }

        return false;
    }

    private void updateHeroPosition(int x, int y) {
        int oldX = mapManager.getHeroX();
        int oldY = mapManager.getHeroY();

        // Заменяем старую позицию героя на пустое место или точку в зависимости от текущего содержимого
        if (mapManager.getMap()[oldY][oldX] == 'H') {
            mapManager.getMap()[oldY][oldX] = mapManager.getMap()[oldY][oldX] == '.' ? '.' : ' ';  // Если это точка, оставляем точку, иначе заменяем на пустое место
        }

        // Обновляем позицию героя на новой клетке
        mapManager.setHeroPosition(x, y);

        // Устанавливаем символ героя на новой позиции
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
                endGame(); // Завершаем игру
            } else {
                // Если герои выиграли, удаляем врага с карты
                removeEnemyFromMap(enemy);
                updateHeroPosition(x, y);
            }

            return true;
        }
        return false;
    }

    private void removeEnemyFromMap(Enemy enemy) {
        // Удаляем врага с карты, если он был побежден
        mapManager.removeEnemy(enemy); // Предполагаем, что метод removeEnemy удаляет врага из карты
        mapManager.setEnemyPosition(-1, -1);
        System.out.println("Враг был удален с карты!");
    }

    private void endGame() {
        System.out.println("Игра завершена.");
        // Дополнительная логика завершения игры, например:
        // - Сохранение статистики
        // - Очистка ресурсов
        // - Вывод финального экрана и т.д.
        System.exit(0); // Завершаем выполнение программы
    }

    public void exitCastle(Character character) {
        CastleManager.isInCastle = false;
        int[] heroCastlePos = findHeroCastlePosition();
        int[] enemyCastlePos = findEnemyCastlePosition();
        int castleX = -1, castleY = -1;
        int heroX = mapManager.getHeroX();
        int heroY = mapManager.getHeroY();

        // Проверяем, находится ли герой в замке
        if (mapManager.getMap()[heroY][heroX] == 'C') { // Замок героя
            mapManager.getMap()[heroY][heroX] = '.'; // Освобождаем клетку, где был герой

            // Если герой в замке героя, выходим из него
            if (heroCastlePos[0] != -1 && heroCastlePos[1] != -1) {
                castleX = heroCastlePos[0];
                castleY = heroCastlePos[1];
                int newY = castleY + 1; // Например, перемещение на клетку ниже

                if (newY < mapManager.getHeight() && mapManager.isWalkable(castleX, newY)) {
                    mapManager.getMap()[newY][castleX] = 'H'; // Размещаем героя в новой клетке
                    mapManager.setHeroPosition(castleX, newY); // Обновляем позицию героя
                    System.out.println("Вы покинули замок героя и переместились на клетку ниже.");
                } else {
                    System.out.println("Вы не можете покинуть замок, нет свободной клетки ниже.");
                }

                mapManager.getMap()[castleY][castleX] = 'С'; // Убираем замок с карты
            }
        } else if (mapManager.getMap()[heroY][heroX] == 'E') { // Замок противника
            mapManager.getMap()[heroY][heroX] = '.'; // Освобождаем клетку, где был герой

            // Если герой в замке противника, выходим из него
            if (enemyCastlePos[0] != -1 && enemyCastlePos[1] != -1) {
                castleX = enemyCastlePos[0];
                castleY = enemyCastlePos[1];
                int newY = castleY + 1; // Например, перемещение на клетку ниже

                if (newY < mapManager.getHeight() && mapManager.isWalkable(castleX, newY)) {
                    mapManager.getMap()[newY][castleX] = 'H'; // Размещаем героя в новой клетке
                    mapManager.setHeroPosition(castleX, newY); // Обновляем позицию героя
                    System.out.println("Вы покинули замок противника и переместились на клетку ниже.");
                } else {
                    System.out.println("Вы не можете покинуть замок, нет свободной клетки ниже.");
                }

                mapManager.getMap()[castleY][castleX] = 'E'; // Убираем замок противника с карты
            }
        } else {
            System.out.println("Вы не находитесь в замке.");
        }

        printMap(); // Печатаем карту один раз, после всех изменений
    }

    private int[] findHeroCastlePosition() {
        for (int y = 0; y < mapManager.getHeight(); y++) {
            for (int x = 0; x < mapManager.getWidth(); x++) {
                if (mapManager.getMap()[y][x] == 'C') { // Замок героя
                    return new int[]{x, y};
                }
            }
        }
        return new int[]{-1, -1}; // Если замок героя не найден
    }

    private int[] findEnemyCastlePosition() {
        for (int y = 0; y < mapManager.getHeight(); y++) {
            for (int x = 0; x < mapManager.getWidth(); x++) {
                if (mapManager.getMap()[y][x] == 'E') { // Замок противника
                    return new int[]{x, y};
                }
            }
        }
        return new int[]{-1, -1}; // Если замок противника не найден
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