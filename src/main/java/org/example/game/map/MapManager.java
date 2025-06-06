package org.example.game.map;

import org.example.game.Player;
import org.example.game.battle.Battle;
import org.example.game.battle.BattleField;
import org.example.game.build.*;
import org.example.game.person.*;
import org.example.game.person.Character;
import org.example.game.save.SaveManager;
import org.example.game.save.GameState;

import java.util.Random;
import java.util.*;
import java.util.stream.Collectors;
import java.io.*;

import static org.example.game.person.Carriage.Direction.*;

public class MapManager {

    private final Scanner scanner;
    private final SaveManager saveManager;
    private final String playerName;
    private final GameState gameState;

    private boolean isFirstEnemyMove = true;
    private boolean first = true;
    char lastwas;
    private int secondStepEnemy = 0;
    private boolean isHeroTurn = true;

    private static final String MAPS_DIRECTORY = "maps/";

    public MapManager(HeroCastle heroCastle, EnemyCastle enemyCastle, Enemy enemy, Hero hero, GameMap gameMap, Road road, Carriage carriage) {
        this.scanner = new Scanner(System.in);
        this.saveManager = new SaveManager();
        this.playerName = hero.getName();

        // Создаем список юнитов для GameState
        List<Unit> allUnits = new ArrayList<>();
        allUnits.addAll(hero.getUnits());
        if (enemy != null && enemy.getUnits() != null) {
            allUnits.addAll(enemy.getUnits());
        }
        
        // Создаем временного игрока для GameState
        Player player = new Player(hero.getName(), hero.getGold());
        this.gameState = new GameState(playerName, player, gameMap, hero, enemy, heroCastle, enemyCastle, allUnits, carriage, road);
        initializeMap(heroCastle, enemyCastle, enemy, hero, gameMap, road, carriage);
    }

    private void initializeMap(HeroCastle heroCastle, EnemyCastle enemyCastle, Enemy enemy, Hero hero, GameMap gameMap, Road road, Carriage carriage) {

        // Размещение препятствий, замков, дорог
        Terrain.placeObstacles(gameMap.getMap(), gameMap.getWidth(), gameMap.getHeight());
        placeCastles(heroCastle, enemyCastle, gameMap);
        placeCarriage(carriage, gameMap);
        road.placeRoad(gameMap.getMap());
        initializeCharacterPositions(enemy, hero, gameMap);
    }

    private void placeCastles(HeroCastle heroCastle, EnemyCastle enemyCastle, GameMap gameMap) {
        gameMap.setCellValue(enemyCastle.getPosition().getX(), enemyCastle.getPosition().getY(), 'E');
    }

    private void placeCarriage(Carriage carriage, GameMap gameMap) {
        gameMap.setCellValue(carriage.getPosition().getX(), carriage.getPosition().getY(), 'D');
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

    public boolean startGame(Hero hero, Enemy enemy, Castle castle, Player player,
                          EnemyCastle enemyCastle, HeroCastle heroCastle, GameMap gameMap,
                          MapManager mapManager, List<Unit> buyUnit, BattleField battleField,
                          List<Unit> allUnits, Carriage carriage) {
        gameMap.printMap();
        Scanner scanner = new Scanner(System.in);

        boolean isHeroTurn = true; // Флаг для отслеживания хода: true - ход героя, false - ход врага

        while (true) {
            System.out.println("\nХод героя:");
            System.out.println("w - вверх");
            System.out.println("s - вниз");
            System.out.println("a - влево");
            System.out.println("d - вправо");
            System.out.println("q - выход");

            System.out.print("Введите команду: ");
            String command = scanner.nextLine();

            if (command.equals("q")) {
                System.out.println("Хотите сохранить игру перед выходом? (y/n)");
                String saveChoice = scanner.nextLine().trim().toLowerCase();
                if (saveChoice.equals("y")) {
                    saveManager.saveGame(playerName, gameState, false);
                }
                System.out.println("Возвращение в главное меню...");
                return true; // Возвращаем true для возврата в главное меню
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
            moveHero(dx, dy, steps, hero, enemy, castle, player, enemyCastle, heroCastle, gameMap, mapManager, buyUnit, hero, battleField, allUnits, carriage);

            gameMap.printMap();
            // Завершаем ход героя и переходим к ходу врага


            System.out.println("\nХод врага:");

            enemyMove(hero, enemy, castle, player, enemyCastle, heroCastle, gameMap, mapManager, buyUnit, battleField, allUnits, carriage);


            moveCarriage(carriage, gameMap, hero);
            gameMap.printMap();
            // Завершаем ход врага и переходим к ходу героя

            // Автосохранение каждые 5 ходов
            if (hero.getCurrentMoves() % 5 == 0) {
                saveManager.saveGame(playerName, gameState, true);
            }
        }
    }



    public void moveCarriage(Carriage carriage, GameMap gameMap, Hero hero) {
        int x = carriage.getPosition().getX();
        int y = carriage.getPosition().getY();

        // Генерация случайной скорости: 1 или 2
        int speed = new Random().nextBoolean() ? 1 : 2;

        // Вычисляем новые координаты
        switch (carriage.getDirection()) {
            case LEFT -> x -= speed;
            case RIGHT -> x += speed;
            case UP -> y -= speed;
            case DOWN -> y += speed;
        }

        // Проверяем, не вышла ли карета за пределы карты
        char[][] map = gameMap.getMap();
        if (x < 0 || x >= map[0].length || y < 0 || y >= map.length) {
            System.out.println("Карета достигла края карты и не может двигаться дальше.");
            return; // Прекращаем движение
        }

        // Применяем перемещение через метод обновления
        updateCarriagePosition(carriage, x, y, gameMap, hero);
    }

    public void enemyMove(Hero hero, Enemy enemy, Castle castle, Player player, EnemyCastle enemyCastle, HeroCastle heroCastle, GameMap gameMap, MapManager mapManager, List<Unit> buyUnit, BattleField battleField, List<Unit> allUnits, Carriage carriage) {
        if (isFirstEnemyMove) {
            CastleManager.exitCastle(enemy, enemyCastle, heroCastle, hero, player, gameMap, castle, mapManager, buyUnit, enemy, battleField, allUnits, carriage);
            isFirstEnemyMove = false;
            secondStepEnemy += 1;
        }
        Random random = new Random();

        System.out.println("Запуск метода enemyMove()...");

        // Проверяем, мертв ли враг
        if (enemy.isDead()) {
            System.out.println("Враг мертв, перемещение не выполняется.");
            return;
        }

        // Список возможных ходов
        List<int[]> validMoves = new ArrayList<>();

        // Проверяем клетку вверх от врага
        int newY = enemy.getX();
        int newX = enemy.getY() - 1;
        if (isRoad(newX, newY, gameMap)) {
            validMoves.add(new int[]{newX, newY});
            System.out.println("Добавлен возможный ход: (" + newX + ", " + newY + ")");
        } else {
            // Выводим символ, если клетка не дорога
            char tile = gameMap.getMap()[newX][newY];
            System.out.println("Клетка (" + newX + ", " + newY + ") не является дорогой. Символ: " + tile);
        }

// Проверяем клетку вниз от врага
        newY = enemy.getX() + 1;
        newX = enemy.getY();
        if (isRoad(newX, newY, gameMap)) {
            validMoves.add(new int[]{newX, newY});
            System.out.println("Добавлен возможный ход: (" + newX + ", " + newY + ")");
        } else {
            // Выводим символ, если клетка не дорога
            char tile = gameMap.getMap()[newX][newY];
            System.out.println("Клетка (" + newX + ", " + newY + ") не является дорогой. Символ: " + tile);
        }

// Проверяем клетку влево от врага
        newX = enemy.getY();
        newY = enemy.getX() - 1;
        if (isRoad(newX, newY, gameMap)) {
            validMoves.add(new int[]{newX, newY});
            System.out.println("Добавлен возможный ход: (" + newX + ", " + newY + ")");
        } else {
            // Выводим символ, если клетка не дорога
            char tile = gameMap.getMap()[newX][newY];
            System.out.println("Клетка (" + newX + ", " + newY + ") не является дорогой. Символ: " + tile);
        }

// Проверяем клетку вправо от врага
        newX = enemy.getY() + 1;
        newY = enemy.getX();
        if (isRoad(newX, newY, gameMap)) {
            validMoves.add(new int[]{newX, newY});
            System.out.println("Добавлен возможный ход: (" + newX + ", " + newY + ")");
        } else {
            // Выводим символ, если клетка не дорога
            char tile = gameMap.getMap()[newX][newY];
            System.out.println("Клетка (" + newX + ", " + newY + ") не является дорогой. Символ: " + tile);
        }
        // Если есть возможные ходы
        if (!validMoves.isEmpty()) {
            // Случайный выбор хода
            int[] move = validMoves.get(random.nextInt(validMoves.size()));

            // Обновляем позицию врага
            updateCharacterPosition(enemy, move[1], move[0], gameMap);

            System.out.println("Враг переместился на (" + move[0] + ", " + move[1] + ").");
        } else {
            System.out.println("Враг не смог найти путь для движения.");
        }

        System.out.println("Метод enemyMove() завершен.");
        isHeroTurn = true;
    }

    public void moveHero(int directionX, int directionY, int distance,
                         Hero hero, Enemy enemy, Castle castle,
                         Player player, EnemyCastle enemyCastle,
                         HeroCastle heroCastle, GameMap gameMap,
                         MapManager mapManager, List<Unit> buyUnit,
                         Character character, BattleField battleField,
                         List<Unit> allUnits, Carriage carriage) {
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
                if (handleCastleEntry(newX, newY, castle, hero, player, enemy,
                        enemyCastle, heroCastle, gameMap, mapManager, buyUnit,
                        character, battleField, allUnits, carriage)) {
                    break;  // Если вход в замок успешен, останавливаем движение
                }
            } else if (isCastleEnemyOnPosition(newX, newY, enemyCastle)) {
                System.out.println("На пути замок врагов.");
                if (handleCastleEntry(newX, newY, castle, hero, player,
                        enemy, enemyCastle, heroCastle, gameMap,
                        mapManager, buyUnit, character,  battleField, allUnits, carriage)) {
                    break;  // Если вход в замок успешен, останавливаем движение
                }
            }

            if (isEnemyOnPosition(newX, newY, enemy)) {
                System.out.println("На пути обнаружен враг. Останавливаемся.");

                checkForBattle(hero, enemy, newX, newY, gameMap, enemyCastle, battleField, allUnits);

                break; // Остановить движение на враге
            }



            // Проверка на препятствие
            if (isObstacleOnPosition(newX, newY, gameMap)) {
                System.out.println("На пути обнаружено препятствие. Останавливаемся.");
                newX -= directionX;
                newY -= directionY;
                break; // Остановить движение на препятствии
            }

            // Проверка на границу карты
            if (!isValidMove(newX, newY, gameMap)) {
                System.out.println("Герой пытается выйти за пределы карты.");
                newX -= directionX;
                newY -= directionY;
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

        isHeroTurn = false;
    }

    // Метод для проверки, является ли клетка препятствием
    private boolean isObstacleOnPosition(int y, int x, GameMap gameMap) {
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

    private boolean isRoad(int x, int y, GameMap gameMap) {
        char[][] map = gameMap.getMap(); // Получаем карту

        // Проверка на выход за пределы карты
        if (x < 0 || x >= map.length || y < 0 || y >= map[0].length) {
            return false; // Если координаты вне карты, клетка недоступна
        }

        char tile = map[x][y]; // Получаем символ клетки

        // Проверяем, является ли клетка дорогой
        return tile == '.'; // Если это точка, значит клетка — дорога
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

    private boolean handleCastleEntry(int x, int y, Castle castle, Hero hero, Player player, Enemy enemy, EnemyCastle enemyCastle, HeroCastle heroCastle, GameMap gameMap, MapManager mapManager, List<Unit> buyUnit, Character character, BattleField battleField, List<Unit> allUnit, Carriage carriage) {
        // Получаем символ на позиции (x, y) карты
        char mapSymbol = gameMap.getMap()[y][x];

        if (mapSymbol == 'C') {
            // Вход в замок героя
            System.out.println("Вы подошли к замку героя! Вход возможен.");
            hero.setPosition(heroCastle.getPosition().getX(), heroCastle.getPosition().getY());
            CastleManager.enterCastle(heroCastle, hero, player, enemy, enemyCastle, heroCastle, gameMap, mapManager, buyUnit, character, battleField, allUnit, carriage);
            return true;
        } else if (x == enemyCastle.getPosition().getX() && y == enemyCastle.getPosition().getY()) {
            if (enemy.isDead()){
                System.out.println("Герой выйграл битву и захватил замок!");
                // Автосохранение после захвата замка
                saveManager.saveGame(playerName, gameState, true);
                endGame();
            }
            if (enemy.getX() == enemyCastle.getPosition().getX() && enemy.getY() == enemyCastle.getPosition().getY()){

                // Запускаем бой
                boolean heroWon = Battle.autoFight(battleField, allUnit);

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
            CastleManager.enterCastle(enemyCastle, hero, player,
                    enemy, enemyCastle, heroCastle, gameMap,
                    mapManager, buyUnit, character,
                    battleField, allUnit, carriage);
            return true;
        }

        // Если ни в один из замков не подошли
        return false;
    }

    private void updateCarriagePosition(Carriage carriage, int x, int y, GameMap gameMap, Character character) {
        char[][] map = gameMap.getMap();
        int oldX = carriage.getPosition().getX();
        int oldY = carriage.getPosition().getY();

        int damage = 10;
        int halfDamage = damage / 2;

        // === 1. Проверка клетки позади (до перемещения)
        int backX = oldX;
        int backY = oldY;

        switch (carriage.getDirection()) {
            case LEFT -> backX += 1;
            case RIGHT -> backX -= 1;
            case UP -> backY += 1;
            case DOWN -> backY -= 1;
        }

        // Если на "задней" клетке стоит герой — наносим половину урона
        if (character.getX() == backX && character.getY() == backY) {
            character.setHealth(character.getHealth() - halfDamage);
            System.out.println("Карета проехала мимо " + character.getName() + " и нанесла " + halfDamage + " урона!");
            System.out.println("Здоровье героя: " + character.getHealth());
        }

        // === 2. Проверка: если на новой клетке находится герой — полный урон, не двигаемся
        if (map[y][x] == 'H' && character.getX() == x && character.getY() == y) {
            character.setHealth(character.getHealth() - damage);
            System.out.println("Карета столкнулась с " + character.getName() + " и нанесла " + damage + " урона!");
            System.out.println("Здоровье героя: " + character.getHealth());
            return;
        }

        // === 3. Проверка на границы карты и изменение направления
        if (x < 1 || x >= map[0].length-1 || y < 0 || y >= map.length-1) {
            // Карета достигла края, меняем направление
            switch (carriage.getDirection()) {
                case LEFT -> carriage.setDirection(Carriage.Direction.RIGHT);
                case RIGHT -> carriage.setDirection(Carriage.Direction.LEFT);
                case UP -> carriage.setDirection(Carriage.Direction.DOWN);
                case DOWN -> carriage.setDirection(Carriage.Direction.UP);
            }

            // Обновляем новые координаты после поворота
            x = oldX;
            y = oldY;
        }

        // === 4. Перемещаем карету
        carriage.setPosition(x, y);
        gameMap.setCellValue(x, y, 'D');

        // === 5. Проверка: если на предыдущей позиции кареты стоял герой — половина урона
        if (character.getX() == oldX && character.getY() == oldY) {
            character.setHealth(character.getHealth() - halfDamage);
            System.out.println("Карета уехала от " + character.getName() + " и нанесла " + halfDamage + " урона при отъезде!");
            System.out.println("Здоровье героя: " + character.getHealth());
        }

        // === 6. Обновляем старую позицию на карте
        if (lastwas == ' ') {
            gameMap.setCellValue(oldX, oldY, ' ');
            lastwas = ' ';
        } else if (first) {
            first = false;
            gameMap.setCellValue(oldX, oldY, ' ');
            lastwas = ' ';
        } else if (map[y][x] == '.' && lastwas == ' ') {
            gameMap.setCellValue(oldX, oldY, ' ');
            lastwas = ' ';
        } else {
            gameMap.setCellValue(oldX, oldY, ' ');
            lastwas = ' ';
        }
    }
    private void updateCharacterPosition(Character character, int x, int y, GameMap gameMap) {
        if (character.getType() == Character.CharacterType.HERO) {
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
        } else {
            char[][] map = gameMap.getMap();
            int oldX = character.getX();
            int oldY = character.getY();

            gameMap.setCellValue(oldX, oldY, '.');
            // Обновляем позицию персонажа
            character.setPosition(x, y);

            // Ставим героя на новую позицию
            gameMap.setCellValue(x, y, 'A');
        }
    }

    private void checkForBattle(Hero hero, Enemy enemy, int x, int y, GameMap gameMap, EnemyCastle enemyCastle, BattleField battleField, List<Unit> allUnit) {


            // Запускаем бой
            boolean heroWon = Battle.autoFight(battleField, allUnit);

            if (!heroWon) {
                System.out.println("Герой проиграл битву!");
                endGame(); // Завершаем игру
            } else {
                // Если герои выиграли, удаляем врага с карты
                removeEnemyFromMap(enemy, gameMap);
                enemy.die();
                updateCharacterPosition(hero, x, y, gameMap);
                hero.addGold(500);
                
                // Автосохранение после победы над врагом
                saveManager.saveGame(playerName, gameState, true);
            }


    }

    public void removeEnemyFromMap(Enemy enemy, GameMap gameMap) {
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
        // Не используем System.exit(0), чтобы позволить вернуться в главное меню
    }

    public static boolean saveMap(GameMap map, String mapName) {
        if (map == null) {
            throw new IllegalArgumentException("Карта не может быть null");
        }
        if (mapName == null || mapName.trim().isEmpty()) {
            throw new IllegalArgumentException("Имя файла не может быть пустым");
        }
        if (mapName.contains("/") || mapName.contains("\\")) {
            throw new IllegalArgumentException("Имя файла содержит недопустимые символы");
        }

        // Убираем лишний префикс maps/, если он есть
        String normalizedMapName = mapName.replace("maps/", "");
        
        String fullPath = MAPS_DIRECTORY + normalizedMapName;
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fullPath))) {
            out.writeObject(map);
            return true;
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении карты: " + e.getMessage());
            return false;
        }
    }

    public static GameMap loadMap(String mapName) {
        if (mapName == null || mapName.trim().isEmpty()) {
            throw new IllegalArgumentException("Имя файла не может быть пустым");
        }

        // Убираем лишний префикс maps/, если он есть
        String normalizedMapName = mapName.replace("maps/", "");
        
        String fullPath = MAPS_DIRECTORY + normalizedMapName;
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(fullPath))) {
            return (GameMap) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Ошибка при загрузке карты: " + e.getMessage());
            throw new RuntimeException("Ошибка при загрузке карты: " + e.getMessage());
        }
    }

    public boolean deleteMap(String mapName) {
        if (mapName == null || mapName.trim().isEmpty()) {
            throw new IllegalArgumentException("Имя файла не может быть пустым");
        }

        File mapFile = new File(MAPS_DIRECTORY + mapName);
        return mapFile.exists() && mapFile.delete();
    }

    public String[] getAvailableMaps() {
        File mapsDir = new File(MAPS_DIRECTORY);
        return mapsDir.list((dir, name) -> name.endsWith(".map"));
    }
}