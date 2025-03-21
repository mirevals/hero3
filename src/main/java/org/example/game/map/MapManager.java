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


    private static boolean first = true;
    char lastwas;



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
        return cell != '#';
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

        if (terrain == '.') {
            return 0;
        }
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
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nКоманды: ");
            System.out.println("w - вверх");
            System.out.println("s - вниз");
            System.out.println("a - влево");
            System.out.println("d - вправо");
            System.out.println("q - выход");

            System.out.print("Введите команду: ");
            String command = scanner.nextLine();

            if (command.equals("q")) {
                System.out.println("Выход из игры.");
                scanner.close();
                return;
            }

            // Получаем максимальную длину хода
            int maxSteps = hero.getAttackRange();
            System.out.println("Максимальная длина хода: " + maxSteps);

            // Запрос на количество клеток для движения
            int steps;
            while (true) {
                System.out.print("Введите количество клеток для движения (не больше " + maxSteps + "): ");
                try {
                    steps = Integer.parseInt(scanner.nextLine());
                    if (steps > 0 && steps <= maxSteps) {
                        break;
                    } else {
                        System.out.println("Некорректный ввод. Введите число от 1 до " + maxSteps + ".");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Некорректный ввод. Введите целое число.");
                }
            }

            // Определение смещения в зависимости от команды
            int dx = 0, dy = 0;
            switch (command) {
                case "w": dy = -1; break;
                case "s": dy = 1; break;
                case "a": dx = -1; break;
                case "d": dx = 1; break;
                default:
                    System.out.println("Неверная команда. Попробуйте снова.");
                    continue;
            }

            // Перемещаем героя
            moveHero(dx, dy, steps, hero, enemy, castle, player, enemyCastle, heroCastle, gameMap, mapManager, buyUnit);

            // Вывод обновленной карты
            gameMap.printMap();
        }
    }

    public void moveHero(int directionX, int directionY, int distance, Hero hero, Enemy enemy, Castle castle, Player player, EnemyCastle enemyCastle, HeroCastle heroCastle, GameMap gameMap, MapManager mapManager, List<Unit> buyUnit) {
        // Проверка на наличие оставшихся шагов перед движением
        if (hero.getCurrentMoves() <= 0) {
            if (!offerToBuySteps(hero)) {
                return;  // Если не купил шаги, игра завершается
            }
        }
        // Проверка, не превышает ли расстояние возможный диапазон атаки
        if (distance > hero.getAttackRange()) {
            System.out.println("Ошибка: Вы не можете переместиться дальше, чем на " + hero.getAttackRange() + " клеток.");
            return;
        }

        int newX = hero.getX();
        int newY = hero.getY();


        // Двигаемся по направлению
        for (int i = 0; i < distance; i++) {
            newX += directionX;
            newY += directionY;

            if (isCastleHeroOnPosition(newX, newY, heroCastle)) {
                System.out.println("На пути замок героев.");
                if (handleCastleEntry(newX, newY, castle, hero, player, enemy, enemyCastle, heroCastle, gameMap, mapManager, buyUnit)) {
                    break;  // Если вход в замок успешен, останавливаем движение
                }
            } else if (isCastleEnemyOnPosition(newX, newY, enemyCastle)) {
                System.out.println("На пути замок врагов.");
                if (handleCastleEntry(newX, newY, castle, hero, player, enemy, enemyCastle, heroCastle, gameMap, mapManager, buyUnit)) {
                    break;  // Если вход в замок успешен, останавливаем движение
                }
            }

            if (isEnemyOnPosition(newX, newY, enemy)) {
                System.out.println("На пути обнаружен враг. Останавливаемся.");
                break; // Остановить движение на враге
            }



            // Проверка на препятствие
            if (isObstacleOnPosition(newX, newY, gameMap)) {
                System.out.println("На пути обнаружено препятствие. Останавливаемся.");
                break; // Остановить движение на препятствии
            }

            // Проверка на границу карты
            if (!isValidMove(newX, newY, gameMap)) {
                System.out.println("Герой пытается выйти за пределы карты.");
                break; // Остановить движение на границе карты
            }

            // Увеличиваем количество сделанных шагов

            // Проверка на количество шагов
            int penalty = getMovementPenalty(newX, newY, gameMap);
            if (hero.getCurrentMoves() < penalty) {
                // Если шагов не хватает, двигаемся только на то количество, которое можем
                break; // Выходим из цикла, так как мы переместились на максимальную возможную клетку
            }
            i++;
        }

        // Вычисляем, сколько шагов использовано
        int penalty = getMovementPenalty(newX, newY, gameMap);
        hero.setCurrentMoves(hero.getCurrentMoves() - penalty);

        // Информация о типе территории
        String terrainType = Terrain.getTerritoryType(newX, newY);
        System.out.println("Герой шагает на территорию: " + terrainType);

        // Обновляем позицию героя и карту
        updateCharacterPosition(hero, newX, newY, gameMap);

        // Вывод оставшихся шагов
        System.out.println("Оставшиеся шаги: " + hero.getCurrentMoves());

        // Проверка на бой
        checkForBattle(hero, enemy, newX, newY, gameMap, enemyCastle);
    }

    // Метод для проверки, является ли клетка препятствием
    private boolean isObstacleOnPosition(int x, int y, GameMap gameMap) {
        char[][] map = gameMap.getMap();
        // Получаем символ на позиции (x, y)
        char tile = map[x][y];

        // Если символ на клетке - это '#', то это препятствие
        if (tile == '#') {
            return true;
        }

        // Если символ не '#', значит, нет препятствия
        return false;
    }

    private boolean isEnemyOnPosition(int x, int y, Enemy enemy) {
        return enemy.getPosition().getX() == x && enemy.getPosition().getY() == y;
    }

    private boolean isCastleHeroOnPosition(int x, int y, HeroCastle heroCastle) {
        return heroCastle.getPosition().getX() == x && heroCastle.getPosition().getY() == y;
    }

    private boolean isCastleEnemyOnPosition(int x, int y, EnemyCastle enemyCastle) {
        return enemyCastle.getPosition().getX() == x && enemyCastle.getPosition().getY() == y;
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
            CastleManager.enterCastle(heroCastle, hero, player, enemy, enemyCastle, heroCastle, gameMap, mapManager, buyUnit);
            return true;
        } else if (x == enemyCastle.getPosition().getX() && y == enemyCastle.getPosition().getY()) {
            if (enemy.isDead()){
                System.out.println("Герой выйграл битву и захватил замок!");
                endGame();
            }
            if (enemy.getX() == enemyCastle.getPosition().getX() && enemy.getY() == enemyCastle.getPosition().getY()){
                BattleField battleField = new BattleField(hero.getUnits(), enemy.getUnits());
                Battle battle = new Battle(battleField);

                // Запускаем бой
                boolean heroWon = battle.autoFight();

                if (!heroWon) {
                    System.out.println("Герой проиграл битву!");
                    enemy.die();
                    endGame(); // Завершаем игру
                } else {
                    // Если герои выиграли, удаляем врага с карты
                    removeEnemyFromMap(enemy, gameMap);
                    System.out.println("Герой выйграл битву и захватил замок!");
                    hero.addGold(500);
                    endGame(); // Завершаем игру

                }

            }
            // Вход в замок противника
            System.out.println("Вы подошли к замку противника! Вход возможен.");
            hero.setPosition(enemyCastle.getPosition().getX(), enemyCastle.getPosition().getY());
            CastleManager.enterCastle(enemyCastle, hero, player, enemy, enemyCastle, heroCastle, gameMap, mapManager, buyUnit);
            return true;
        }

        // Если ни в один из замков не подошли
        return false;
    }

    private void updateCharacterPosition(Character character, int x, int y, GameMap gameMap) {
        char[][] map = gameMap.getMap();
        int oldX = character.getX();
        int oldY = character.getY();

        // Проверяем, что было на предыдущей клетке
        if (lastwas == '.') {
            gameMap.setCellValue(oldX, oldY, '.');
            lastwas = '.';
        } else if (first) {
            first = false;
            gameMap.setCellValue(oldX, oldY, '.');
            lastwas = '.';
        } else if (map[x][y] == '.' && lastwas == ' ') {
            gameMap.setCellValue(oldX, oldY, ' ');
            lastwas = '.';
        } else {
            gameMap.setCellValue(oldX, oldY, '.');
            lastwas = ' ';
        }

        // Обновляем позицию персонажа
        character.setPosition(x, y);

        // Ставим героя на новую позицию
        gameMap.setCellValue(x, y, 'H');
    }

    private void checkForBattle(Hero hero, Enemy enemy, int x, int y, GameMap gameMap, EnemyCastle enemyCastle) {
        if (!enemy.isDead() && x == enemy.getX() && y == enemy.getY() && enemy.getY() != enemyCastle.getPosition().getY() && enemy.getX() != enemyCastle.getPosition().getX()) {
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
                enemy.die();
                updateCharacterPosition(hero, x, y, gameMap);
                hero.addGold(500);
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