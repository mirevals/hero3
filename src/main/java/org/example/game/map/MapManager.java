package org.example.game.map;

import org.example.game.battle.Battle;
import org.example.game.battle.BattleField;
import org.example.game.build.Castle;
import org.example.game.build.CastleManager;
import org.example.game.build.EnemyCastle;
import org.example.game.build.HeroCastle;
import org.example.game.person.Character;
import org.example.game.person.Enemy;
import org.example.game.person.Hero;

import java.util.Scanner;

public class MapManager {

    private final Scanner scanner;

    private final int width;
    private final int height;
    private final char[][] map;

    // Координаты героя и врага
    private static int heroX;
    private static int heroY;
    private int enemyX;
    private int enemyY;


    public MapManager(int width, int height, HeroCastle heroCastle, EnemyCastle enemyCastle, Hero hero, Enemy enemy) {
        this.scanner = new Scanner(System.in);
        this.width = width;
        this.height = height;
        this.map = new char[height][width];

        initializeMap(heroCastle, enemyCastle, hero, enemy);
    }

    private void initializeMap(HeroCastle heroCastle, EnemyCastle enemyCastle, Hero hero, Enemy enemy) {
        // Заполняем карту символами для замков, препятствий и дорог
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                map[y][x] = ' ';  // Все клетки сначала пустые
            }
        }

        // Размещение препятствий, замков, дорог
        Terrain.placeObstacles(map, width, height);
        placeCastles(heroCastle, enemyCastle);
        Road road = new Road(width / 6, height / 4, 5 * width / 6, height / 4);
        road.placeRoad(map);
        initializeCharacterPositions(hero, enemy);
    }

    private void placeCastles(HeroCastle heroCastle, EnemyCastle enemyCastle) {
        // Получаем позиции замков
        Position heroCastlePos = heroCastle.getPosition();
        Position enemyCastlePos = enemyCastle.getPosition();

        // Размещение замков на карте
        map[heroCastlePos.getY()][heroCastlePos.getX()] = 'C';  // Замок игрока
        map[enemyCastlePos.getY()][enemyCastlePos.getX()] = 'E';  // Замок противника
    }

    private void initializeCharacterPositions(Hero hero, Enemy enemy) {
        // Позиция героя
        heroX = hero.getX();
        heroY = hero.getY();
        map[heroY][heroX] = 'H';  // Размещение героя

        // Позиция противника
        enemyX = enemy.getX();
        enemyY = enemy.getY();
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
        int enemyX = enemy.getX();
        int enemyY = enemy.getY();

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

    public void startGame(Character character, Hero hero, Enemy enemy, Castle.CastleType castleType) {
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
                case "w": moveHero(0, -1, character, hero, enemy, castleType); break;
                case "s": moveHero(0, 1, character, hero, enemy, castleType); break;
                case "a": moveHero(-1, 0, character, hero, enemy, castleType); break;
                case "d": moveHero(1, 0, character, hero, enemy, castleType); break;
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

    public boolean moveHero(int dx, int dy, Character character, Hero hero, Enemy enemy, Castle.CastleType castleType) {
        // Проверка на наличие оставшихся шагов перед движением
        if (character.getCurrentMoves() <= 0) {
            if (!offerToBuySteps(character)) {
                return false;  // Если не купил шаги, игра завершается
            }
        }

        int newX = character.getX() + dx;
        int newY = character.getY() + dy;

        // Проверка на возможность движения (например, блокируется ли что-то, валидность перемещения и замок)
        if (!canMove(character) || !isValidMove(newX, newY) || handleCastleEntry(newX, newY, castleType)) {
            return false; // Прерываем, если какие-то условия не выполнены
        }

        // Вычисление штрафа за территорию
        int penalty = getMovementPenalty(newX, newY);

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
        updateCharacterPosition(character ,newX, newY);

        // Вывод оставшихся шагов
        System.out.println("Оставшиеся шаги: " + character.getCurrentMoves());

        // Проверка на бой
        return !checkForBattle(hero, enemy, newX, newY);
    }


    private boolean offerToBuySteps(Character character) {
        while (true) {  // Блокируем выполнение до тех пор, пока не будет принято решение
            System.out.println("У героя не осталось шагов.");
            System.out.println("Хотите купить 10 шагов за 50 золота? (y/n)");

            String choice = scanner.nextLine();
            if ("y".equalsIgnoreCase(choice)) {
                if (character.getGold() >= 50) {
                    character.setGold(character.getGold() - 50);
                    int newMoves = character.getCurrentMoves() + 10;

                    // Ограничиваем количество перемещений максимальным значением
                    if (newMoves > character.getMaxMoves()) {
                        newMoves = character.getMaxMoves();
                    }

                    character.setCurrentMoves(newMoves);
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
        return character.getCurrentMoves() > 0;
    }

    private boolean isValidMove(int x, int y) {
        return x >= 0 && x < getWidth() && y >= 0 && y < getHeight() && isWalkable(x, y);
    }

    private boolean handleCastleEntry(int x, int y, Castle.CastleType castleType) {

        if (castleType == Castle.CastleType.HERO) {
            // Вход в замок героя
            System.out.println("Вы подошли к замку героя! Вход возможен.");
            CastleManager.enterCastle(castleType);
            return true;
        } else if (castleType == Castle.CastleType.ENEMY) {
            // Вход в замок противника
            System.out.println("Вы подошли к замку противника! Вход невозможен.");
            CastleManager.enterCastle(castleType);

            return true;
        }

        return false;
    }

    private void updateCharacterPosition(Character character, int x, int y) {
        int oldX = character.getX();
        int oldY = character.getY();

        // Заменяем старую позицию героя на пустое место или точку в зависимости от текущего содержимого
        if (getMap()[oldY][oldX] == 'H') {
            getMap()[oldY][oldX] = getMap()[oldY][oldX] == '.' ? '.' : ' ';  // Если это точка, оставляем точку, иначе заменяем на пустое место
        }

        // Обновляем позицию героя на новой клетке
        character.setPosition(x, y);

        // Устанавливаем символ героя на новой позиции
        getMap()[y][x] = 'H';
    }

    private boolean checkForBattle(Hero hero, Enemy enemy, int x, int y) {
        if (x == getEnemyX() && y == getEnemyY()) {
            BattleField battleField = new BattleField(hero.getUnits(), enemy.getUnits());
            Battle battle = new Battle(battleField);

            // Запускаем бой
            boolean heroWon = battle.autoFight();

            if (!heroWon) {
                System.out.println("Герой проиграл битву!");
                endGame(); // Завершаем игру
            } else {
                // Если герои выиграли, удаляем врага с карты
                removeEnemyFromMap(enemy);
                updateCharacterPosition(hero, x, y);
            }

            return true;
        }
        return false;
    }

    private void removeEnemyFromMap(Enemy enemy) {
        // Удаляем врага с карты, если он был побежден
        removeEnemy(enemy); // Предполагаем, что метод removeEnemy удаляет врага из карты
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


    public BattleField enterBattleField(Character hero, Enemy enemy) {
        return new BattleField(hero.getUnits(), enemy.getUnits());
    }

    public char[][] getMap() {
        return map;
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