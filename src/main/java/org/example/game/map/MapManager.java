package org.example.game.map;

import org.example.game.Player;
import org.example.game.battle.Battle;
import org.example.game.battle.BattleField;
import org.example.game.build.*;
import org.example.game.person.Character;
import org.example.game.person.Enemy;
import org.example.game.person.Hero;
import org.example.game.person.Unit;

import java.util.List;
import java.util.Scanner;

public class MapManager {

    private final Scanner scanner;





    public MapManager(HeroCastle heroCastle, EnemyCastle enemyCastle, Enemy enemy, Hero hero, GameMap gameMap, Road road) {
        this.scanner = new Scanner(System.in);

        initializeMap(heroCastle, enemyCastle, enemy, hero, gameMap, road);
    }

    private void initializeMap(HeroCastle heroCastle, EnemyCastle enemyCastle, Enemy enemy, Hero hero, GameMap gameMap, Road road) {

        // Размещение препятствий, замков, дорог
        Terrain.placeObstacles(gameMap.getMap(), gameMap.getWidth(), gameMap.getHeight());
        placeCastles(heroCastle, enemyCastle, gameMap);
        road.placeRoad(gameMap.getMap());
        initializeCharacterPositions(enemy, hero, gameMap);
    }

    private void placeCastles(HeroCastle heroCastle, EnemyCastle enemyCastle, GameMap gameMap) {
        gameMap.setCellValue(enemyCastle.getPosition().getX(), enemyCastle.getPosition().getY(), 'E');
    }

    private void initializeCharacterPositions(Enemy enemy, Hero hero, GameMap gameMap) {
        gameMap.setCellValue(enemy.getX(), enemy.getY(), 'A');
    }

    public boolean isWalkable(int x, int y, GameMap gameMap) {
        char cell = gameMap.getMap()[y][x];
        return cell != '#' && cell != ' ';  // Проверка на препятствие и пустую клетку
    }

    public void removeEnemy(Enemy enemy, GameMap gameMap) {


        // Убираем символ врага с карты
        if (enemy.getX() >= 0 && enemy.getY() >= 0 && enemy.getX() < gameMap.getWidth() && enemy.getY() < gameMap.getHeight()) {
            gameMap.setCellValue(enemy.getY(), enemy.getX(), ' ');
            System.out.println("Враг удален с позиции: (" + enemy.getX() + ", " + enemy.getY() + ")");
        }
    }

    public int getMovementPenalty(int x, int y, GameMap gameMap) {
        char terrain = gameMap.getMap()[y][x];  // Получаем тип текущей клетки

        // Геройская территория
        if (x < gameMap.getWidth() / 3) {
            return 0;  // Нет штрафа на своей территории
        }

        // Вражеская территория
        else if (x > 2 * gameMap.getWidth() / 3) {
            return 1;  // Минимальный штраф на территории врага
        }
        // Нейтральная территория
        return 2;  // Штраф на нейтральной территории
    }

    public void startGame(Hero hero, Enemy enemy, Castle castle, Player player, EnemyCastle enemyCastle, HeroCastle heroCastle, GameMap gameMap, MapManager mapManager, List<Unit> buyUnit) {
        gameMap.printMap();

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
                case "w": moveHero(0, -1, hero, enemy, castle, player, enemyCastle, heroCastle, gameMap, mapManager, buyUnit); break;
                case "s": moveHero(0, 1, hero, enemy, castle, player, enemyCastle, heroCastle, gameMap, mapManager, buyUnit); break;
                case "a": moveHero(-1, 0, hero, enemy, castle, player, enemyCastle, heroCastle, gameMap, mapManager, buyUnit); break;
                case "d": moveHero(1, 0, hero, enemy, castle, player, enemyCastle, heroCastle, gameMap, mapManager, buyUnit); break;
                case "q":
                    System.out.println("Выход из игры.");
                    scanner.close();
                    return;
                default:
                    System.out.println("Неверная команда. Попробуйте снова.");
                    continue;
            }

            gameMap.printMap();
        }
    }

    public void moveHero(int dx, int dy, Hero hero, Enemy enemy, Castle castle, Player player, EnemyCastle enemyCastle, HeroCastle heroCastle, GameMap gameMap, MapManager mapManager, List<Unit> buyUnit) {
        // Проверка на наличие оставшихся шагов перед движением
        if (hero.getCurrentMoves() <= 0) {
            if (!offerToBuySteps(hero)) {
                return;  // Если не купил шаги, игра завершается
            }
        }

        int newX = hero.getX() + dx;
        int newY = hero.getY() + dy;

        // Проверка на возможность движения
        if (!canMove(hero) || !isValidMove(newX, newY, gameMap) || handleCastleEntry(newX, newY, castle, hero, player, enemy, enemyCastle, heroCastle, gameMap, mapManager, buyUnit)) {
            return;
        }

        // Проверяем, хватает ли шагов
        int penalty = getMovementPenalty(newX, newY, gameMap);
        if (hero.getCurrentMoves() < penalty && offerToBuySteps(hero)) {
            System.out.println("Недостаточно шагов для перемещения на эту территорию.");
            return;
        }

        // Информация о типе территории
        String terrainType = Terrain.getTerritoryType(newX, newY);
        System.out.println("Герой шагает на территорию: " + terrainType);

        // Списываем штраф за территорию
        hero.setCurrentMoves(hero.getCurrentMoves() - penalty);

        // Обновляем позицию героя и карту
        updateCharacterPosition(hero, newX, newY, gameMap);

        // Вывод оставшихся шагов
        System.out.println("Оставшиеся шаги: " + hero.getCurrentMoves());

        // Проверка на бой
        checkForBattle(hero, enemy, newX, newY, gameMap);
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

    private boolean isValidMove(int x, int y, GameMap gameMap) {
        return x >= 0 && x < gameMap.getWidth() && y >= 0 && y < gameMap.getHeight() && isWalkable(x, y, gameMap);
    }

    private boolean handleCastleEntry(int x, int y, Castle castle, Hero hero, Player player, Enemy enemy, EnemyCastle enemyCastle, HeroCastle heroCastle, GameMap gameMap, MapManager mapManager, List<Unit> buyUnit) {
        // Получаем символ на позиции (x, y) карты
        char mapSymbol = gameMap.getMap()[y][x];

        if (mapSymbol == 'C') {
            // Вход в замок героя
            System.out.println("Вы подошли к замку героя! Вход возможен.");
            hero.setPosition(heroCastle.getPosition().getX(), heroCastle.getPosition().getY());
            CastleManager.enterCastle(castle, hero, player, enemy, enemyCastle, heroCastle, gameMap, mapManager, buyUnit);
            return true;
        } else if (mapSymbol == 'E') {
            // Вход в замок противника
            System.out.println("Вы подошли к замку противника! Вход невозможен.");
            hero.setPosition(enemyCastle.getPosition().getX(), enemyCastle.getPosition().getY());
            CastleManager.enterCastle(castle, hero, player, enemy, enemyCastle, heroCastle, gameMap, mapManager, buyUnit);
            return true;
        }

        // Если ни в один из замков не подошли
        return false;
    }

    private void updateCharacterPosition(Character character, int x, int y, GameMap gameMap) {
        char[][] map = gameMap.getMap();
        int oldX = character.getX();
        int oldY = character.getY();

        // Убираем героя с предыдущей клетки
        map[oldY][oldX] = '.';

        // Обновляем позицию персонажа
        character.setPosition(x, y);

        // Ставим героя на новую позицию
        map[y][x] = 'H';
    }

    private void checkForBattle(Hero hero, Enemy enemy, int x, int y, GameMap gameMap) {
        if (x == enemy.getX() && y == enemy.getY()) {
            BattleField battleField = new BattleField(hero.getUnits(), enemy.getUnits());
            Battle battle = new Battle(battleField);

            // Запускаем бой
            boolean heroWon = battle.autoFight();

            if (!heroWon) {
                System.out.println("Герой проиграл битву!");
                endGame(); // Завершаем игру
            } else {
                // Если герои выиграли, удаляем врага с карты
                removeEnemyFromMap(enemy, gameMap);
                updateCharacterPosition(hero, x, y, gameMap);
            }

        }
    }

    private void removeEnemyFromMap(Enemy enemy, GameMap gameMap) {
        // Удаляем врага с карты, если он был побежден
        removeEnemy(enemy, gameMap); // Предполагаем, что метод removeEnemy удаляет врага из карты
        enemy.setPosition(-1, -1);
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


}