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
    private final Scanner scanner;

    public GameMap(int width, int height) {
        this.mapManager = new MapManager(width, height);
        this.scanner = new Scanner(System.in);
    }

    Enemy enemy = CastleManager.getEnemy();

    public void startGame() {
        CastleManager.enterCastle(this, 'C');

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
                case "w": moveHero(0, -1, CastleManager.getSelectedHero(), enemy); break;
                case "s": moveHero(0, 1, CastleManager.getSelectedHero(), enemy); break;
                case "a": moveHero(-1, 0, CastleManager.getSelectedHero(), enemy); break;
                case "d": moveHero(1, 0, CastleManager.getSelectedHero(), enemy); break;
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
        // Проверка на наличие оставшихся шагов перед движением
        if (character.getCurrentMoves() <= 0) {
            if (!offerToBuySteps(character)) {
                return false;  // Если не купил шаги, игра завершается
            }
        }

        int newX = mapManager.getHeroX() + dx;
        int newY = mapManager.getHeroY() + dy;

        // Проверка на возможность движения (например, блокируется ли что-то, валидность перемещения и замок)
        if (!canMove(character) || !isValidMove(newX, newY) || handleCastleEntry(character, newX, newY)) {
            return false; // Прерываем, если какие-то условия не выполнены
        }

        // Вычисление штрафа за территорию
        int penalty = mapManager.getMovementPenalty(newX, newY);

        // Проверка, хватает ли шагов на передвижение
        if (character.getCurrentMoves() < penalty) {
            System.out.println("Недостаточно шагов для перемещения на эту территорию.");
            System.out.println("Недостаточно шагов для перемещения на эту территорию.");
            return false;  // Прерываем, если не хватает шагов
        }

        // Информация о типе территории
        String terrainType = Terrain.getTerritoryType(newX, newY);
        System.out.println("Герой шагает на территорию: " + terrainType);

        // Списываем штраф за территорию
        character.setCurrentMoves(character.getCurrentMoves() - penalty);

        // Обновляем позицию героя
        updateHeroPosition(newX, newY);

        // Вывод оставшихся шагов
        System.out.println("Оставшиеся шаги: " + character.getCurrentMoves());

        // Проверка на бой
        return !checkForBattle(character, enemy, newX, newY);
    }

    private boolean offerToBuySteps(Character character) {
        while (true) {  // Блокируем выполнение до тех пор, пока не будет принято решение
            System.out.println("У героя не осталось шагов.");
            System.out.println("Хотите купить 10 шагов за 50 золота? (y/n)");

            String choice = scanner.nextLine();
            if ("y".equalsIgnoreCase(choice)) {
                if (character.getGold() >= 50) {
                    character.setGold(character.getGold() - 50);
                    character.setCurrentMoves(character.getCurrentMoves() + 10);
                    System.out.println("Вы купили 10 шагов. Оставшееся золото: " + character.getGold());
                    return true;
                } else {
                    System.out.println("Недостаточно золота!");
                    endGame();
                    return false;
                }
            } else if ("n".equalsIgnoreCase(choice)) {
                System.out.println("Вы отказались от покупки шагов.");
                endGame();
                return false;
            } else {
                System.out.println("Некорректный выбор. Пожалуйста, введите 'y' или 'n'.");
            }
        }
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
            CastleManager.enterCastle( this, 'C'); // Входим в замок героя
            return true;
        } else if (castleType == 'E') {
            // Вход в замок противника
            System.out.println("Вы подошли к замку противника! Вход невозможен.");
            CastleManager.enterCastle(this, 'E'); // Входим в замок героя

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
        CastleManager.getSelectedHero().setHeroPosition(x, y);

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
        enemy.setEnemyPosition(-1, -1);
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

    public int[] findHeroCastlePosition() {
        for (int y = 0; y < mapManager.getHeight(); y++) {
            for (int x = 0; x < mapManager.getWidth(); x++) {
                if (mapManager.getMap()[y][x] == 'C') { // Замок героя
                    return new int[]{x, y};
                }
            }
        }
        return new int[]{-1, -1}; // Если замок героя не найден
    }

    public int[] findEnemyCastlePosition() {
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

    private boolean canMoveEnemy(int dx, int dy) {
        int newX = mapManager.getEnemyX() + dx;
        int newY = mapManager.getEnemyY() + dy;
        return isValidMove(newX, newY);
    }

    public int getWidth() {
        return mapManager.getWidth();
    }

    public int getHeight() {
        return mapManager.getHeight();
    }

}