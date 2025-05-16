package org.example.game.save;

import org.example.game.Player;
import org.example.game.battle.BattleField;
import org.example.game.build.*;
import org.example.game.map.*;
import org.example.game.person.*;
import org.junit.jupiter.api.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.example.game.build.Shop.availableBuildings;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SaveLoadStateTest {
    private static final String TEST_PLAYER = "test_player";
    private static final String TEST_SAVE = "test_save.sav";
    private SaveManager saveManager;
    private GameState gameState;
    private GameMap gameMap;
    private Hero hero;
    private Enemy enemy;
    private HeroCastle heroCastle;
    private EnemyCastle enemyCastle;
    private List<Unit> allUnits;
    private Carriage carriage;
    private Road road;
    private Player player;

    @BeforeEach
    void setUp() {
        // Создаем базовые объекты
        gameMap = new GameMap(20, 20);
        player = new Player(TEST_PLAYER, 1000);
        
        // Инициализируем списки юнитов
        List<Unit> heroUnits = new ArrayList<>();
        List<Unit> enemyUnits = new ArrayList<>();
        
        // Создаем героя и врага с правильными параметрами
        hero = new Hero(TEST_PLAYER, 10, Team.HERO, 1000, gameMap.getWidth(), gameMap.getHeight(), 
                       100, 100, 100, 3, heroUnits);
        enemy = new Enemy("Enemy", 5, Team.ENEMY, 100, gameMap.getWidth(), gameMap.getHeight(),
                         100, 100, 100, 1, enemyUnits);
        
        // Создаем замки с правильными параметрами
        heroCastle = new HeroCastle(gameMap.getHeight(), gameMap.getWidth());
        enemyCastle = new EnemyCastle(gameMap.getHeight(), gameMap.getWidth());
        
        // Создаем карету и дорогу
        carriage = new Carriage(new Position(10, 10), 1, 10, Carriage.Direction.DOWN);
        road = new Road(2, 2, 17, 17);
        saveManager = new SaveManager();

        // Инициализируем армии
        initializeArmies();

        // Размещаем объекты на карте
        placeObjectsOnMap();

        // Добавляем постройки в замки
        initializeCastleBuildings();

        // Создаем состояние игры
        gameState = new GameState(TEST_PLAYER, player, gameMap, hero, enemy, 
                                heroCastle, enemyCastle, allUnits, carriage, road);

        // Создаем директорию для сохранений
        new File("saves").mkdirs();
    }

    @AfterEach
    void tearDown() {
        // Удаляем тестовое сохранение
        File testSave = new File("saves/" + TEST_SAVE);
        if (testSave.exists()) {
            testSave.delete();
        }
    }

    private void initializeArmies() {
        allUnits = new ArrayList<>();
        
        // Создаем отряды героя
        Unit heroWarrior = new Unit(Unit.UnitType.WARRIOR, 100, 100, 1, 10, Team.HERO, 'W', 100);
        Unit heroArcher = new Unit(Unit.UnitType.ARCHER, 100, 80, 2, 15, Team.HERO, 'A', 120);
        Unit heroMage = new Unit(Unit.UnitType.MAGE, 80, 120, 1, 12, Team.HERO, 'M', 150);
        
        hero.addUnit(heroWarrior);
        hero.addUnit(heroArcher);
        hero.addUnit(heroMage);
        
        // Создаем отряды врага
        Unit enemyWarrior = new Unit(Unit.UnitType.WARRIOR, 100, 100, 1, 10, Team.ENEMY, 'W', 100);
        Unit enemyArcher = new Unit(Unit.UnitType.ARCHER, 100, 80, 2, 15, Team.ENEMY, 'A', 120);
        
        enemy.addUnit(enemyWarrior);
        enemy.addUnit(enemyArcher);
        
        // Добавляем все отряды в общий список
        allUnits.addAll(hero.getUnits());
        allUnits.addAll(enemy.getUnits());
    }

    private void placeObjectsOnMap() {
        // Размещаем замки
        gameMap.setCellValue(heroCastle.getPosition().getX(), heroCastle.getPosition().getY(), 'C');
        gameMap.setCellValue(enemyCastle.getPosition().getX(), enemyCastle.getPosition().getY(), 'E');
        
        // Размещаем героя и врага
        gameMap.setCellValue(hero.getX(), hero.getY(), 'H');
        gameMap.setCellValue(enemy.getX(), enemy.getY(), 'A');
        
        // Размещаем карету
        gameMap.setCellValue(carriage.getPosition().getX(), carriage.getPosition().getY(), 'D');
        
        // Размещаем дорогу
        road.placeRoad(gameMap.getMap());
    }

    private void initializeCastleBuildings() {
        // Добавляем постройки в замок героя
        heroCastle.addBuilding(new Tavern());
        heroCastle.addBuilding(new GuardPost());
        
        // Добавляем постройки в замок врага
        enemyCastle.addBuilding(new GuardPost());
        enemyCastle.addBuilding(availableBuildings.get(0)); // Используем доступные постройки из Shop
    }

    @Test
    @Order(1)
    @DisplayName("Тест сохранения и загрузки базовых параметров игры")
    void testSaveLoadBasicParameters() {
        // Изменяем состояние некоторых юнитов перед сохранением
        Unit heroWarrior = hero.getUnits().get(0);
        int initialHealth = heroWarrior.getHealth();
        int newHealth = 50;

        // Сохраняем игру
        saveManager.saveGame(TEST_PLAYER, gameState, false);

        // Получаем список сохранений и берем последнее
        List<String> saves = saveManager.getAvailableSaves(TEST_PLAYER);
        assertFalse(saves.isEmpty(), "Должно быть хотя бы одно сохранение");
        String saveName = saves.stream()
                .filter(name -> name.startsWith("save_" + TEST_PLAYER))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Не найдено сохранение для игрока " + TEST_PLAYER));

        // Загружаем сохранение
        GameState loadedState = saveManager.loadGame(TEST_PLAYER, saveName);
        
        // Проверяем базовые параметры
        assertNotNull(loadedState, "Загруженное состояние не должно быть null");
        assertEquals(TEST_PLAYER, loadedState.getPlayerName(), "Имя игрока должно совпадать");
        assertEquals(1000, loadedState.getPlayer().getGold(), "Количество золота должно совпадать");
        assertEquals(gameMap.getWidth(), loadedState.getGameMap().getWidth(), "Ширина карты должна совпадать");
        assertEquals(gameMap.getHeight(), loadedState.getGameMap().getHeight(), "Высота карты должна совпадать");
    }

    @Test
    @Order(2)
    @DisplayName("Тест сохранения и загрузки персонажей")
    void testSaveLoadCharacters() {
        // Изменяем состояние некоторых юнитов перед сохранением
        Unit heroWarrior = hero.getUnits().get(0);
        int initialHealth = heroWarrior.getHealth();
        int newHealth = 50;

        // Сохраняем игру
        saveManager.saveGame(TEST_PLAYER, gameState, false);

        // Получаем список сохранений и берем последнее
        List<String> saves = saveManager.getAvailableSaves(TEST_PLAYER);
        assertFalse(saves.isEmpty(), "Должно быть хотя бы одно сохранение");
        String saveName = saves.stream()
                .filter(name -> name.startsWith("save_" + TEST_PLAYER))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Не найдено сохранение для игрока " + TEST_PLAYER));

        // Загружаем сохранение
        GameState loadedState = saveManager.loadGame(TEST_PLAYER, saveName);
        
        // Проверяем героя
        Hero loadedHero = loadedState.getHero();
        assertEquals(hero.getName(), loadedHero.getName(), "Имя героя должно совпадать");
        assertEquals(hero.getX(), loadedHero.getX(), "Позиция X героя должна совпадать");
        assertEquals(hero.getY(), loadedHero.getY(), "Позиция Y героя должна совпадать");
        assertEquals(hero.getHealth(), loadedHero.getHealth(), "Здоровье героя должно совпадать");
        
        // Проверяем врага
        Enemy loadedEnemy = loadedState.getEnemy();
        assertEquals(enemy.getName(), loadedEnemy.getName(), "Имя врага должно совпадать");
        assertEquals(enemy.getX(), loadedEnemy.getX(), "Позиция X врага должна совпадать");
        assertEquals(enemy.getY(), loadedEnemy.getY(), "Позиция Y врага должна совпадать");
        assertEquals(enemy.getHealth(), loadedEnemy.getHealth(), "Здоровье врага должно совпадать");
    }

    @Test
    @Order(3)
    @DisplayName("Тест сохранения и загрузки армий")
    void testSaveLoadArmies() {
        // Изменяем состояние некоторых юнитов перед сохранением
        Unit heroWarrior = hero.getUnits().get(0);
        int initialHealth = heroWarrior.getHealth();
        int newHealth = 50;

        // Сохраняем игру
        saveManager.saveGame(TEST_PLAYER, gameState, false);

        // Получаем список сохранений и берем последнее
        List<String> saves = saveManager.getAvailableSaves(TEST_PLAYER);
        assertFalse(saves.isEmpty(), "Должно быть хотя бы одно сохранение");
        String saveName = saves.stream()
                .filter(name -> name.startsWith("save_" + TEST_PLAYER))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Не найдено сохранение для игрока " + TEST_PLAYER));

        // Загружаем сохранение
        GameState loadedState = saveManager.loadGame(TEST_PLAYER, saveName);
        
        // Проверяем армию героя
        List<Unit> loadedHeroUnits = loadedState.getHero().getUnits();
        assertEquals(hero.getUnits().size(), loadedHeroUnits.size(), "Количество отрядов героя должно совпадать");
        for (int i = 0; i < hero.getUnits().size(); i++) {
            assertEquals(hero.getUnits().get(i).getType(), 
                        loadedHeroUnits.get(i).getType(),
                        "Тип отряда героя должен совпадать");
        }
        
        // Проверяем армию врага
        List<Unit> loadedEnemyUnits = loadedState.getEnemy().getUnits();
        assertEquals(enemy.getUnits().size(), loadedEnemyUnits.size(), "Количество отрядов врага должно совпадать");
        for (int i = 0; i < enemy.getUnits().size(); i++) {
            assertEquals(enemy.getUnits().get(i).getType(), 
                        loadedEnemyUnits.get(i).getType(),
                        "Тип отряда врага должен совпадать");
        }
    }

    @Test
    @Order(4)
    @DisplayName("Тест сохранения и загрузки замков и построек")
    void testSaveLoadCastlesAndBuildings() {
        // Изменяем состояние некоторых юнитов перед сохранением
        Unit heroWarrior = hero.getUnits().get(0);
        int initialHealth = heroWarrior.getHealth();
        int newHealth = 50;

        // Сохраняем игру
        saveManager.saveGame(TEST_PLAYER, gameState, false);

        // Получаем список сохранений и берем последнее
        List<String> saves = saveManager.getAvailableSaves(TEST_PLAYER);
        assertFalse(saves.isEmpty(), "Должно быть хотя бы одно сохранение");
        String saveName = saves.stream()
                .filter(name -> name.startsWith("save_" + TEST_PLAYER))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Не найдено сохранение для игрока " + TEST_PLAYER));

        // Загружаем сохранение
        GameState loadedState = saveManager.loadGame(TEST_PLAYER, saveName);
        
        // Проверяем замок героя
        HeroCastle loadedHeroCastle = loadedState.getHeroCastle();
        assertEquals(heroCastle.getPosition().getX(), loadedHeroCastle.getPosition().getX(),
                "Позиция X замка героя должна совпадать");
        assertEquals(heroCastle.getPosition().getY(), loadedHeroCastle.getPosition().getY(),
                "Позиция Y замка героя должна совпадать");
        assertEquals(heroCastle.getConstructedBuildings().size(), 
                    loadedHeroCastle.getConstructedBuildings().size(),
                    "Количество построек в замке героя должно совпадать");
        
        // Проверяем замок врага
        EnemyCastle loadedEnemyCastle = loadedState.getEnemyCastle();
        assertEquals(enemyCastle.getPosition().getX(), loadedEnemyCastle.getPosition().getX(),
                "Позиция X замка врага должна совпадать");
        assertEquals(enemyCastle.getPosition().getY(), loadedEnemyCastle.getPosition().getY(),
                "Позиция Y замка врага должна совпадать");
        assertEquals(enemyCastle.getConstructedBuildings().size(), 
                    loadedEnemyCastle.getConstructedBuildings().size(),
                    "Количество построек в замке врага должно совпадать");
    }


    @Test
    @Order(6)
    @DisplayName("Тест сохранения и загрузки кареты")
    void testSaveLoadCarriage() {
        // Изменяем состояние некоторых юнитов перед сохранением
        Unit heroWarrior = hero.getUnits().get(0);
        int initialHealth = heroWarrior.getHealth();
        int newHealth = 50;

        // Сохраняем игру
        saveManager.saveGame(TEST_PLAYER, gameState, false);

        // Получаем список сохранений и берем последнее
        List<String> saves = saveManager.getAvailableSaves(TEST_PLAYER);
        assertFalse(saves.isEmpty(), "Должно быть хотя бы одно сохранение");
        String saveName = saves.stream()
                .filter(name -> name.startsWith("save_" + TEST_PLAYER))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Не найдено сохранение для игрока " + TEST_PLAYER));

        // Загружаем сохранение
        GameState loadedState = saveManager.loadGame(TEST_PLAYER, saveName);
        
        Carriage loadedCarriage = loadedState.getCarriage();
        assertEquals(carriage.getPosition().getX(), loadedCarriage.getPosition().getX(),
                "Позиция X кареты должна совпадать");
        assertEquals(carriage.getPosition().getY(), loadedCarriage.getPosition().getY(),
                "Позиция Y кареты должна совпадать");
        assertEquals(carriage.getDirection(), loadedCarriage.getDirection(),
                "Направление движения кареты должно совпадать");
    }

    @Test
    @Order(7)
    @DisplayName("Тест сохранения и загрузки состояния построек")
    void testSaveLoadBuildingsState() {
        // Изменяем состояние некоторых юнитов перед сохранением
        Unit heroWarrior = hero.getUnits().get(0);
        int initialHealth = heroWarrior.getHealth();
        int newHealth = 50;

        // Сохраняем игру
        saveManager.saveGame(TEST_PLAYER, gameState, false);

        // Получаем список сохранений и берем последнее
        List<String> saves = saveManager.getAvailableSaves(TEST_PLAYER);
        assertFalse(saves.isEmpty(), "Должно быть хотя бы одно сохранение");
        String saveName = saves.stream()
                .filter(name -> name.startsWith("save_" + TEST_PLAYER))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Не найдено сохранение для игрока " + TEST_PLAYER));

        // Загружаем сохранение
        GameState loadedState = saveManager.loadGame(TEST_PLAYER, saveName);
        
        // Проверяем состояние построек после загрузки
        HeroCastle loadedHeroCastle = loadedState.getHeroCastle();
        List<Building> heroBuildings = heroCastle.getConstructedBuildings();
        List<Building> loadedHeroBuildings = loadedHeroCastle.getConstructedBuildings();
        
        assertEquals(heroBuildings.size(), loadedHeroBuildings.size(), 
                    "Количество построек должно совпадать");
        for (int i = 0; i < heroBuildings.size(); i++) {
            assertEquals(heroBuildings.get(i).getName(), loadedHeroBuildings.get(i).getName(),
                        "Названия построек должны совпадать");
        }
    }

    @Test
    @Order(8)
    @DisplayName("Тест сохранения и загрузки состояния юнитов")
    void testSaveLoadUnitsState() {
        // Изменяем состояние некоторых юнитов перед сохранением
        Unit heroWarrior = hero.getUnits().get(0);
        int initialHealth = heroWarrior.getHealth();
        int newHealth = 50;
        
        // Сохраняем игру
        saveManager.saveGame(TEST_PLAYER, gameState, false);
        
        // Получаем список сохранений и берем последнее
        List<String> saves = saveManager.getAvailableSaves(TEST_PLAYER);
        assertFalse(saves.isEmpty(), "Должно быть хотя бы одно сохранение");
        String saveName = saves.stream()
            .filter(name -> name.startsWith("save_" + TEST_PLAYER))
            .findFirst()
            .orElseThrow(() -> new AssertionError("Не найдено сохранение для игрока " + TEST_PLAYER));
        
        // Загружаем сохранение
        GameState loadedState = saveManager.loadGame(TEST_PLAYER, saveName);
        assertNotNull(loadedState, "Загруженное состояние не должно быть null");
        
        // Проверяем состояние юнитов после загрузки
        Unit loadedHeroWarrior = loadedState.getHero().getUnits().get(0);
        assertEquals(initialHealth, loadedHeroWarrior.getHealth(), "Здоровье воина должно совпадать");
    }




    @Test
    @Order(11)
    @DisplayName("Тест корректности данных в автосохранении")
    void testAutoSaveDataIntegrity() {
        // Создаем специфическое состояние для проверки
        hero.setGold(12345);
        Unit newUnit = new Unit(Unit.UnitType.MAGE, 90, 150, 2, 15, Team.HERO, 'M', 200);
        hero.addUnit(newUnit);
        heroCastle.addBuilding(new GuardPost());
        
        gameState = new GameState(TEST_PLAYER, player, gameMap, hero, enemy, 
                                heroCastle, enemyCastle, allUnits, carriage, road);

        // Делаем автосохранение
        saveManager.saveGame(TEST_PLAYER, gameState, true);

        // Получаем последнее автосохранение
        String lastAutoSave = saveManager.getAvailableSaves(TEST_PLAYER)
            .stream()
            .filter(name -> name.startsWith("auto_" + TEST_PLAYER))
            .max(String::compareTo)
            .orElseThrow();

        // Загружаем состояние
        GameState loadedState = saveManager.loadGame(TEST_PLAYER, lastAutoSave);

        // Проверяем все аспекты сохраненного состояния
        assertNotNull(loadedState, "Загруженное состояние не должно быть null");
        assertEquals(12345, loadedState.getHero().getGold(), "Количество золота должно совпадать");
        assertEquals(hero.getUnits().size(), loadedState.getHero().getUnits().size(), 
            "Количество юнитов должно совпадать");
        
        // Проверяем последнего добавленного юнита
        Unit loadedUnit = loadedState.getHero().getUnits().get(loadedState.getHero().getUnits().size() - 1);
        assertEquals(Unit.UnitType.MAGE, loadedUnit.getType(), "Тип юнита должен совпадать");
        assertEquals(90, loadedUnit.getHealth(), "Здоровье юнита должно совпадать");
        
        // Проверяем постройки
        assertEquals(heroCastle.getConstructedBuildings().size(), 
            loadedState.getHeroCastle().getConstructedBuildings().size(),
            "Количество построек должно совпадать");
    }

    @Test
    @Order(12)
    @DisplayName("Тест загрузки автосохранения при отсутствии файла")
    void testAutoSaveLoadingWithMissingFile() {
        // Пытаемся загрузить несуществующее автосохранение
        GameState loadedState = saveManager.loadGame(TEST_PLAYER, "auto_nonexistent.sav");
        assertNull(loadedState, "При загрузке несуществующего файла должен возвращаться null");
    }
} 