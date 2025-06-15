package org.example.game.map;

import org.example.account.Account;
import org.example.game.Player;
import org.example.game.battle.Battle;
import org.example.game.battle.BattleField;
import org.example.game.build.*;
import org.example.game.person.*;
import org.example.game.person.Character;
import org.example.game.save.SaveManager;
import org.example.game.save.GameState;
import org.example.game.Game; // Import Game class
import org.example.game.Virus; // Add this import

import java.util.Random;
import java.util.*;
import java.util.stream.Collectors;
import java.io.*;

import static org.example.game.person.Carriage.Direction.*;

public class MapManager {

    private final Scanner scanner;
    private Game game;
    private final HeroCastle heroCastle;
    private final EnemyCastle enemyCastle;
    private final GameMap gameMap;
    private final String playerName;
    private final SaveManager saveManager;
    private GameState gameState;
    private boolean infectionStateInitialized = false;

    private boolean isFirstEnemyMove = true;
    private boolean first = true;
    char lastwas;
    private int secondStepEnemy = 0;
    private boolean isHeroTurn = true;

    private static final String MAPS_DIRECTORY = "maps/";

    private long gameTimeInMinutes = 0; // Игровое время в минутах
    private long lastTimeUpdate = 0; // Время последнего обновления в реальном времени
    private static final long REAL_TIME_TO_GAME_TIME = 100; // 100мс реального времени = 1 минута игрового времени

    public MapManager(HeroCastle heroCastle, EnemyCastle enemyCastle, Enemy enemy, Hero hero, GameMap gameMap, Road road, Carriage carriage, Game game) {
        this.scanner = new Scanner(System.in);
        this.heroCastle = heroCastle;
        this.enemyCastle = enemyCastle;
        this.game = game;
        this.gameMap = gameMap;
        this.playerName = hero.getName();
        this.saveManager = new SaveManager();
        
        // Создаем список юнитов для GameState
        List<Unit> allUnits = new ArrayList<>();
        allUnits.addAll(hero.getUnits());
        if (enemy != null && enemy.getUnits() != null) {
            allUnits.addAll(enemy.getUnits());
        }
        
        // Создаем временного игрока для GameState
        Player player = new Player(hero.getName(), hero.getGold());
        
        // Инициализируем GameState
        this.gameState = new GameState(
            playerName,
            player,
            gameMap,
            hero,
            enemy,
            heroCastle,
            enemyCastle,
            allUnits,
            carriage,
            road,
            false,
            new ArrayList<>()
        );

        placeCastles(heroCastle, enemyCastle, gameMap);
        placeCarriage(carriage, gameMap);
        initializeCharacterPositions(enemy, hero, gameMap);
        road.placeRoad(gameMap.getMap());
    }

    public MapManager(HeroCastle heroCastle, EnemyCastle enemyCastle, Enemy enemy, Hero hero, GameMap gameMap, Road road, Carriage carriage, boolean isAccountInfected, List<Virus> accountViruses) {
        this.scanner = new Scanner(System.in);
        this.heroCastle = heroCastle;
        this.enemyCastle = enemyCastle;
        this.game = null;
        this.gameMap = gameMap;
        this.playerName = hero.getName();
        this.saveManager = new SaveManager();
        
        // Создаем список юнитов для GameState
        List<Unit> allUnits = new ArrayList<>();
        allUnits.addAll(hero.getUnits());
        if (enemy != null && enemy.getUnits() != null) {
            allUnits.addAll(enemy.getUnits());
        }
        
        // Создаем временного игрока для GameState
        Player player = new Player(hero.getName(), hero.getGold());
        
        // Инициализируем GameState с учетом состояния заражения
        this.gameState = new GameState(
            playerName,
            player,
            gameMap,
            hero,
            enemy,
            heroCastle,
            enemyCastle,
            allUnits,
            carriage,
            road,
            isAccountInfected,
            accountViruses,
            gameState != null ? gameState.getGameTimeInMinutes() : 0 // Используем сохраненное время или 0 для новой игры
        );

        // Устанавливаем начальное время
        this.gameTimeInMinutes = gameState != null ? gameState.getGameTimeInMinutes() : 0;
        this.lastTimeUpdate = System.currentTimeMillis();

        placeCastles(heroCastle, enemyCastle, gameMap);
        placeCarriage(carriage, gameMap);
        initializeCharacterPositions(enemy, hero, gameMap);
        road.placeRoad(gameMap.getMap());
    }

    public void setGame(Game game) {
        this.game = game;
        // Обновляем время при установке игры
        if (gameState != null) {
            this.gameTimeInMinutes = gameState.getGameTimeInMinutes();
            this.lastTimeUpdate = System.currentTimeMillis();
        }
        updateInfectionState();
    }

    public void updateInfectionState() {
        if (!infectionStateInitialized && game != null && game.getAccountManager() != null) {
            Account currentAccount = game.getAccountManager().getCurrentAccount().orElse(null);
            if (currentAccount != null) {
                System.out.println("Обновление состояния заражения для " + playerName);
                System.out.println("Заражен: " + currentAccount.isInfected());
                System.out.println("Количество вирусов: " + currentAccount.getViruses().size());
                
                // Create new GameState with current infection state
                this.gameState = new GameState(
                    playerName,
                    gameState.getPlayer(),
                    gameMap,
                    gameState.getHero(),
                    gameState.getEnemy(),
                    gameState.getHeroCastle(),
                    gameState.getEnemyCastle(),
                    gameState.getAllUnits(),
                    gameState.getCarriage(),
                    gameState.getRoad(),
                    currentAccount.isInfected(),
                    currentAccount.getViruses()
                );
                infectionStateInitialized = true;
            }
        }
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
        gameMap.setCellValue(heroCastle.getPosition().getX(), heroCastle.getPosition().getY(), 'H');
        gameMap.setCellValue(enemyCastle.getPosition().getX(), enemyCastle.getPosition().getY(), 'E');
    }

    private void placeCarriage(Carriage carriage, GameMap gameMap) {
        gameMap.setCellValue(carriage.getPosition().getX(), carriage.getPosition().getY(), 'D');
    }

    private void initializeCharacterPositions(Enemy enemy, Hero hero, GameMap gameMap) {
        gameMap.setCellValue(hero.getX(), hero.getY(), 'H');
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
            CastleManager.exitCastle(enemy, enemyCastle, heroCastle, hero, player, gameMap, castle, mapManager, buyUnit, enemy, battleField, allUnits, carriage, this.scanner);
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
        int targetX = enemy.getX(); // Initialize targetX
        int targetY = enemy.getY(); // Initialize targetY

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
        newY = enemy.getX();
        newX = enemy.getY() + 1;
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
        newX = enemy.getY();
        newY = enemy.getX() + 1;
        if (isRoad(newX, newY, gameMap)) {
            validMoves.add(new int[]{newX, newY});
            System.out.println("Добавлен возможный ход: (" + newX + ", " + newY + ")");
        } else {
            // Выводим символ, если клетка не дорога
            char tile = gameMap.getMap()[newX][newY];
            System.out.println("Клетка (" + newX + ", " + newY + ") не является дорогой. Символ: " + tile);
        }

        // Перемещаем врага, если есть доступные ходы
        if (!validMoves.isEmpty()) {
            int[] move = validMoves.get(random.nextInt(validMoves.size()));
            targetX = move[0];
            targetY = move[1];

            // Проверяем, не наступает ли враг на героя или препятствие
            if (isObstacleOnPosition(targetY, targetX, gameMap) || isHeroOnPosition(targetX, targetY, hero)) {
                System.out.println("Враг не может переместиться на (" + targetX + ", " + targetY + ") из-за препятствия или героя.");
            } else {
                // Убираем символ врага с текущей позиции
                gameMap.setCellValue(enemy.getX(), enemy.getY(), ' ');

                // Обновляем позицию врага
                enemy.setX(targetX);
                enemy.setY(targetY);

                // Размещаем символ врага на новой позиции
                gameMap.setCellValue(enemy.getX(), enemy.getY(), 'A');

                System.out.println("Враг переместился на: (" + targetX + ", " + targetY + ")");
            }
        } else {
            System.out.println("Врагу некуда ходить.");
        }
        // Проверяем, находится ли герой рядом с врагом
        checkForBattle(hero, enemy, targetX, targetY, gameMap, enemyCastle, battleField, allUnits);
    }

    public void moveHero(int dx, int dy, int steps, Hero hero, Enemy enemy, HeroCastle heroCastle,
                        Player player, EnemyCastle enemyCastle, HeroCastle heroCastle2, GameMap gameMap,
                        MapManager mapManager, List<Unit> units, Hero hero2, BattleField battleField,
                        List<Unit> allUnits, Carriage carriage) {
        updateGameTime(); // Обновляем игровое время перед каждым ходом
        Position oldPosition = hero.getPosition();
        Position newPosition = new Position(oldPosition.getX() + dx, oldPosition.getY() + dy);

        // Check if the new position is valid
        if (isValidPosition(newPosition, gameMap)) {
            // Store old position for virus spreading
            Position previousPosition = new Position(oldPosition.getX(), oldPosition.getY());
            
            // Check for virus at new position before moving
            if (game != null) {
                for (Virus virus : game.getActiveVirusesOnMap()) {
                    if (virus.getPosition() != null && 
                        virus.getPosition().getX() == newPosition.getX() && 
                        virus.getPosition().getY() == newPosition.getY()) {
                        game.playerPicksUpVirus(virus);
                        // Don't move hero if virus was picked up
                        return;
                    }
                }
            }
            
            // Проверка входа в замок по символу (если на новой позиции символ равен 'C' или 'E')
            char cell = gameMap.getCellValue(newPosition.getX(), newPosition.getY());
            if (cell == 'C' || cell == 'E') {
                // Move hero
                hero.setX(newPosition.getX());
                hero.setY(newPosition.getY());
                gameMap.setCellValue(oldPosition.getX(), oldPosition.getY(), ' ');
                gameMap.setCellValue(newPosition.getX(), newPosition.getY(), 'H');
                CastleManager.enterCastle(heroCastle, hero, player, enemy, enemyCastle, heroCastle, gameMap, this, units, hero, battleField, allUnits, carriage, this.scanner);
                return;
            }

            // Проверка входа в здания
            if (cell == 'O' || cell == 'K' || cell == 'B') {
                // Move hero
                hero.setX(newPosition.getX());
                hero.setY(newPosition.getY());
                gameMap.setCellValue(oldPosition.getX(), oldPosition.getY(), ' ');
                gameMap.setCellValue(newPosition.getX(), newPosition.getY(), 'H');

                // Находим здание по координатам
                Building building = findBuildingAt(newPosition.getX(), newPosition.getY());
                if (building != null) {
                    enterBuilding(building, hero, player);
                }
                return;
            }

            // Move hero if not entering castle or building
            hero.setX(newPosition.getX());
            hero.setY(newPosition.getY());
            gameMap.setCellValue(oldPosition.getX(), oldPosition.getY(), ' ');
            gameMap.setCellValue(newPosition.getX(), newPosition.getY(), 'H');

            // Handle virus spreading if game instance exists
            if (game != null) {
                game.handleHeroMovement(previousPosition, newPosition);
            }

            // Check for battle after movement
            checkForBattle(hero, enemy, newPosition.getX(), newPosition.getY(), gameMap, enemyCastle, battleField, allUnits);
        }
    }

    private boolean isObstacleOnPosition(int y, int x, GameMap gameMap) {
        return gameMap.getCellValue(x, y) == '#';
    }

    private boolean isHeroOnPosition(int x, int y, Hero hero) {
        return hero.getX() == x && hero.getY() == y;
    }

    private boolean isRoad(int x, int y, GameMap gameMap) {
        if (!gameMap.isValidPosition(x, y)) {
            return false;
        }
        char cell = gameMap.getCellValue(x, y);
        return cell == 'R';
    }

    private boolean isEnemyOnPosition(int x, int y, Enemy enemy) {
        return enemy.getX() == x && enemy.getY() == y;
    }

    public boolean isCastleHeroOnPosition(int x, int y, HeroCastle heroCastle) {
        return heroCastle.getPosition().getX() == x && heroCastle.getPosition().getY() == y;
    }

    public boolean isCastleEnemyOnPosition(int x, int y, EnemyCastle enemyCastle) {
        return enemyCastle.getPosition().getX() == x && enemyCastle.getPosition().getY() == y;
    }

    private boolean isVirusOnPosition(int x, int y, GameMap gameMap) {
        return gameMap.getCellValue(x, y) == 'V';
    }

    public boolean offerToBuySteps(Character character) {
        System.out.println("Хотите купить дополнительные шаги за 10 золота? (y/n)");
        String choice = scanner.nextLine().trim().toLowerCase();
        if (choice.equals("y")) {
            if (character.getGold() >= 10) {
                character.spendGold(10);
                character.addMoves(5); // Добавляем 5 шагов за 10 золота
                System.out.println("Куплено 5 дополнительных шагов.");
                return true;
            } else {
                System.out.println("Недостаточно золота для покупки шагов.");
                return false;
            }
        }
        return false;
    }

    private boolean canMove(Character character) {
        return character.getCurrentMoves() > 0;
    }

    private boolean isValidMove(int x, int y, GameMap gameMap) {
        return gameMap.isValidPosition(x, y) && gameMap.getCellValue(x, y) != '#';
    }

    private boolean handleCastleEntry(int x, int y, Castle castle, Hero hero, Player player, Enemy enemy, EnemyCastle enemyCastle, HeroCastle heroCastle, GameMap gameMap, MapManager mapManager, List<Unit> buyUnit, Character character, BattleField battleField, List<Unit> allUnit, Carriage carriage, Scanner scanner) {
        if (isCastleHeroOnPosition(x, y, heroCastle)) {
            System.out.println("Вы вошли в замок героя!");
            CastleManager.enterCastle(castle, hero, player, enemy, enemyCastle, heroCastle, gameMap, mapManager, buyUnit, character, battleField, allUnit, carriage, scanner);
            return true;
        } else if (isCastleEnemyOnPosition(x, y, enemyCastle)) {
            System.out.println("Вы вошли в замок врага! Начинается битва!");
            Battle.autoFight(battleField, allUnit);
            if (hero.isDead()) {
                System.out.println("Герой погиб в битве. Игра окончена.");
                endGame();
                return false; // Герой погиб, игра окончена
            } else if (enemy.isDead()) {
                System.out.println("Вы победили врага в его замке!");
                // Награды за победу
                hero.addGold(500);
                // Удалить врага с карты
                removeEnemyFromMap(enemy, gameMap);

                return true; // Вернуть true, так как игра продолжается после победы
            }
            return true;
        }
        return false;
    }

    private void updateCarriagePosition(Carriage carriage, int x, int y, GameMap gameMap, Character character) {
        // Очищаем предыдущую позицию кареты
        gameMap.setCellValue(carriage.getPosition().getX(), carriage.getPosition().getY(), ' ');

        // Обновляем позицию кареты
        carriage.getPosition().setX(x);
        carriage.getPosition().setY(y);

        // Размещаем карету на новой позиции
        gameMap.setCellValue(carriage.getPosition().getX(), carriage.getPosition().getY(), 'D');
        System.out.println("Карета переместилась на: (" + carriage.getPosition().getX() + ", " + carriage.getPosition().getY() + ")");

        // Если карета достигла героя, добавляем золото
        if (carriage.getPosition().getX() == character.getX() && carriage.getPosition().getY() == character.getY()) {
            character.addGold(carriage.collectGold());
            System.out.println("Карета доставила золото герою!");
            // Перемещаем карету обратно на базу после доставки
            carriage.getPosition().setX(2 * gameMap.getWidth() / 3);
            carriage.getPosition().setY(gameMap.getHeight() / 2);
            carriage.setDirection(LEFT);
        }
    }

    private void updateCharacterPosition(Character character, int x, int y, GameMap gameMap) {
        // Убираем символ персонажа с карты
        if (character.getX() >= 0 && character.getY() >= 0 && character.getX() < gameMap.getWidth() && character.getY() < gameMap.getHeight()) {
            gameMap.setCellValue(character.getY(), character.getX(), ' ');
        }

        // Обновляем позицию персонажа
        character.setX(x);
        character.setY(y);

        // Размещаем символ персонажа на новой позиции
        gameMap.setCellValue(character.getY(), character.getX(), character.getType().getSymbol());
        System.out.println(character.getName() + " переместился на: (" + character.getX() + ", " + character.getY() + ")");
    }

    private void checkForBattle(Hero hero, Enemy enemy, int x, int y, GameMap gameMap, EnemyCastle enemyCastle, BattleField battleField, List<Unit> allUnit) {
        if (isEnemyOnPosition(x, y, enemy)) {
            System.out.println("Враг обнаружен! Начинается битва!");
            Battle.autoFight(battleField, allUnit);
            if (hero.isDead()) {
                System.out.println("Герой погиб в битве. Игра окончена.");
                endGame();
            } else if (enemy.isDead()) {
                System.out.println("Враг побежден!");
                // Награды за победу
                hero.addGold(500);
                // Удалить врага с карты
                removeEnemyFromMap(enemy, gameMap);

            }
        }
    }

    public void removeEnemyFromMap(Enemy enemy, GameMap gameMap) {
        if (enemy != null && enemy.getX() >= 0 && enemy.getY() >= 0 && enemy.getX() < gameMap.getWidth() && enemy.getY() < gameMap.getHeight()) {
            gameMap.setCellValue(enemy.getX(), enemy.getY(), ' '); // Убираем символ врага с карты
            System.out.println("Враг был удален с карты!");
        }
    }

    private void endGame() {
        System.out.println("Игра окончена.");
        System.exit(0); // Выход из приложения
    }

    public static boolean saveMap(GameMap map, String mapName) {
        try {
            String mapsDirectory = "maps";
            File directory = new File(mapsDirectory);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(mapsDirectory + File.separator + mapName + ".map"))) {
                out.writeObject(map);
                System.out.println("Карта \"" + mapName + "\" успешно сохранена.");
                return true;
            }
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении карты: " + e.getMessage());
            return false;
        }
    }

    public static GameMap loadMap(String mapName) {
        try {
            String mapsDirectory = "maps";
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(mapsDirectory + File.separator + mapName + ".map"))) {
                GameMap loadedMap = (GameMap) in.readObject();
                System.out.println("Карта \"" + mapName + "\" успешно загружена.");
                return loadedMap;
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Ошибка при загрузке карты: " + e.getMessage());
            return null;
        }
    }

    public boolean deleteMap(String mapName) {
        File mapFile = new File(MAPS_DIRECTORY + mapName + ".map");
        if (mapFile.exists()) {
            return mapFile.delete();
        }
        return false;
    }

    public String[] getAvailableMaps() {
        File mapsDir = new File(MAPS_DIRECTORY);
        return mapsDir.list((dir, name) -> name.endsWith(".map"));
    }

    private boolean isValidPosition(Position position, GameMap gameMap) {
        return position.getX() >= 0 && position.getX() < gameMap.getWidth() &&
               position.getY() >= 0 && position.getY() < gameMap.getHeight() &&
               gameMap.getCellValue(position.getX(), position.getY()) != '#';
    }

    private Building findBuildingAt(int x, int y) {
        // Ищем здание в списке построенных зданий обоих замков
        for (Building building : heroCastle.getConstructedBuildings()) {
            if (building.getX() == x && building.getY() == y) {
                return building;
            }
        }
        for (Building building : enemyCastle.getConstructedBuildings()) {
            if (building.getX() == x && building.getY() == y) {
                return building;
            }
        }
        return null;
    }

    private void enterBuilding(Building building, Hero hero, Player player) {
        if (building == null) {
            System.out.println("Здание не найдено!");
            return;
        }

        if (game == null) {
            System.out.println("Ошибка: игра не инициализирована!");
            return;
        }

        System.out.println("\nВы вошли в " + building.getName());
        
        if (building instanceof Hotel) {
            showHotelServices((Hotel) building, hero, player);
        } else if (building instanceof Cafe) {
            showCafeServices((Cafe) building, hero, player);
        } else if (building instanceof BarberShop) {
            showBarberShopServices((BarberShop) building, hero, player);
        }
    }

    private void showHotelServices(Hotel hotel, Hero hero, Player player) {
        System.out.println("\nДоступные услуги в отеле:");
        for (Service service : hotel.getAvailableServices()) {
            System.out.println(service.toString());
        }
        
        while (true) {
            System.out.println("\nВыберите услугу (введите номер) или 0 для выхода:");
            try {
                int choice = Integer.parseInt(scanner.nextLine());
                if (choice == 0) break;
                
                List<Service> services = hotel.getAvailableServices();
                if (choice > 0 && choice <= services.size()) {
                    Service selectedService = services.get(choice - 1);
                    if (player.getGold() >= selectedService.getGoldCost()) {
                        player.spendGold(selectedService.getGoldCost());
                        System.out.println("Вы приобрели услугу: " + selectedService.getName());
                        // Применяем эффект услуги
                        applyServiceEffect(selectedService.getEffect(), hero);
                    } else {
                        System.out.println("Недостаточно золота!");
                    }
                } else {
                    System.out.println("Неверный выбор!");
                }
            } catch (NumberFormatException e) {
                System.out.println("Пожалуйста, введите число!");
            }
        }
    }

    private void showCafeServices(Cafe cafe, Hero hero, Player player) {
        System.out.println("\nДоступные услуги в кафе:");
        for (Service service : cafe.getAvailableServices()) {
            System.out.println(service.toString());
        }
        
        while (true) {
            System.out.println("\nВыберите услугу (введите номер) или 0 для выхода:");
            try {
                int choice = Integer.parseInt(scanner.nextLine());
                if (choice == 0) break;
                
                List<Service> services = cafe.getAvailableServices();
                if (choice > 0 && choice <= services.size()) {
                    Service selectedService = services.get(choice - 1);
                    if (player.getGold() >= selectedService.getGoldCost()) {
                        player.spendGold(selectedService.getGoldCost());
                        System.out.println("Вы приобрели услугу: " + selectedService.getName());
                        // Применяем эффект услуги
                        applyServiceEffect(selectedService.getEffect(), hero);
                    } else {
                        System.out.println("Недостаточно золота!");
                    }
                } else {
                    System.out.println("Неверный выбор!");
                }
            } catch (NumberFormatException e) {
                System.out.println("Пожалуйста, введите число!");
            }
        }
    }

    private void showBarberShopServices(BarberShop barberShop, Hero hero, Player player) {
        System.out.println("\nДоступные услуги в парикмахерской:");
        for (Service service : barberShop.getAvailableServices()) {
            System.out.println(service.toString());
        }
        
        while (true) {
            System.out.println("\nВыберите услугу (введите номер) или 0 для выхода:");
            try {
                int choice = Integer.parseInt(scanner.nextLine());
                if (choice == 0) break;
                
                List<Service> services = barberShop.getAvailableServices();
                if (choice > 0 && choice <= services.size()) {
                    Service selectedService = services.get(choice - 1);
                    if (player.getGold() >= selectedService.getGoldCost()) {
                        player.spendGold(selectedService.getGoldCost());
                        System.out.println("Вы приобрели услугу: " + selectedService.getName());
                        // Применяем эффект услуги
                        applyServiceEffect(selectedService.getEffect(), hero);
                    } else {
                        System.out.println("Недостаточно золота!");
                    }
                } else {
                    System.out.println("Неверный выбор!");
                }
            } catch (NumberFormatException e) {
                System.out.println("Пожалуйста, введите число!");
            }
        }
    }

    private void applyServiceEffect(ServiceEffect effect, Hero hero) {
        switch (effect.getType()) {
            case HEALTH_BOOST:
                hero.setHealth(hero.getHealth() + effect.getValue());
                System.out.println("Здоровье увеличено на " + effect.getValue());
                break;
            case MOVEMENT_BOOST:
                hero.addMoves(effect.getValue());
                System.out.println("Добавлено " + effect.getValue() + " шагов");
                break;
            case CASTLE_CAPTURE_TIME_REDUCTION:
                // Здесь можно добавить логику для уменьшения времени захвата замка
                System.out.println("Время захвата замка уменьшено на " + effect.getValue() + " минут");
                break;
        }
    }

    public void updateGameTime() {
        long currentTime = System.currentTimeMillis();
        if (lastTimeUpdate == 0) {
            lastTimeUpdate = currentTime;
            return;
        }

        long timeDiff = currentTime - lastTimeUpdate;
        if (timeDiff >= REAL_TIME_TO_GAME_TIME) {
            gameTimeInMinutes++;
            lastTimeUpdate = currentTime;
            System.out.println("\nПрошло игрового времени: " + gameTimeInMinutes + " минут");
        }
    }

    public long getGameTimeInMinutes() {
        return gameTimeInMinutes;
    }

    public String getFormattedGameTime() {
        long hours = gameTimeInMinutes / 60;
        long minutes = gameTimeInMinutes % 60;
        return String.format("%02d:%02d", hours, minutes);
    }
}