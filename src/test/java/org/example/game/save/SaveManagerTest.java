package org.example.game.save;

import org.example.game.*;
import org.example.game.map.*;
import org.example.game.person.*;
import org.example.game.build.*;
import org.junit.jupiter.api.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SaveManagerTest {
    private static final String TEST_PLAYER = "test_player";
    private static SaveManager saveManager;
    private static GameState gameState;
    private static GameMap gameMap;
    private static Hero hero;
    private static Enemy enemy;
    private static HeroCastle heroCastle;
    private static EnemyCastle enemyCastle;
    private static List<Unit> allUnits;
    private static Carriage carriage;
    private static Road road;

    @BeforeAll
    static void setUp() {
        saveManager = new SaveManager();
        gameMap = new GameMap(10, 10);
        
        // Создаем юнитов
        Unit heroUnit = new Unit(Unit.UnitType.WARRIOR, 100, 100, 1, 10, Team.HERO, 'W', 100);
        List<Unit> heroUnits = new ArrayList<>();
        heroUnits.add(heroUnit);
        
        // Инициализируем тестовое состояние игры
        Player player = new Player(TEST_PLAYER, 1000);
        hero = new Hero(TEST_PLAYER, 10, Team.HERO, 1000, 10, 10, 100, 100, 100, 3, heroUnits);
        enemy = new Enemy("Enemy", 5, Team.ENEMY, 100, 10, 10, 100, 100, 100, 1, new ArrayList<>());
        
        heroCastle = new HeroCastle(10, 10);
        enemyCastle = new EnemyCastle(10, 10);
        heroCastle.addBuilding(new Tavern());
        heroCastle.addBuilding(new GuardPost());
        
        allUnits = new ArrayList<>(heroUnits);
        carriage = new Carriage(new Position(5, 0), 1, 10, Carriage.Direction.DOWN);
        road = new Road(2, 2, 8, 2);
        
        gameState = new GameState(TEST_PLAYER, player, gameMap, hero, enemy, heroCastle, enemyCastle, allUnits, carriage, road);
    }

    @AfterAll
    static void tearDown() {
        // Удаляем тестовые сохранения
        File savesDir = new File("saves");
        if (savesDir.exists()) {
            for (File file : savesDir.listFiles((dir, name) -> name.startsWith("test_"))) {
                file.delete();
            }
        }
    }

    @Test
    @Order(1)
    @DisplayName("Тест ручного сохранения")
    void testManualSave() {
        saveManager.saveGame(TEST_PLAYER, gameState, false);
        List<String> saves = saveManager.getAvailableSaves(TEST_PLAYER);
        assertFalse(saves.isEmpty());
        assertTrue(saves.stream().anyMatch(name -> name.startsWith("save_" + TEST_PLAYER)));
    }

    @Test
    @Order(2)
    @DisplayName("Тест автосохранения")
    void testAutoSave() {
        saveManager.saveGame(TEST_PLAYER, gameState, true);
        List<String> saves = saveManager.getAvailableSaves(TEST_PLAYER);
        assertFalse(saves.isEmpty());
        assertTrue(saves.stream().anyMatch(name -> name.startsWith("auto_" + TEST_PLAYER)));
    }

    @Test
    @Order(3)
    @DisplayName("Тест загрузки сохранения")
    void testLoadGame() {
        // Очищаем все постройки перед тестом
        heroCastle.getConstructedBuildings().clear();
        // Добавляем ровно 2 здания
        heroCastle.addBuilding(new Tavern());
        heroCastle.addBuilding(new GuardPost());
        
        // Сначала сохраняем
        saveManager.saveGame(TEST_PLAYER, gameState, false);
        List<String> saves = saveManager.getAvailableSaves(TEST_PLAYER);
        String saveName = saves.stream()
            .filter(name -> name.startsWith("save_" + TEST_PLAYER))
            .findFirst()
            .orElseThrow();
        
        // Затем загружаем
        GameState loadedState = saveManager.loadGame(TEST_PLAYER, saveName);
        
        assertNotNull(loadedState);
        assertEquals(TEST_PLAYER, loadedState.getPlayerName());
        assertEquals(TEST_PLAYER, loadedState.getHero().getName());
        assertEquals(1000, loadedState.getPlayer().getGold());
        
        // Проверяем количество и типы зданий
        List<Building> buildings = loadedState.getHeroCastle().getConstructedBuildings();
        assertTrue(buildings.stream().anyMatch(b -> b instanceof Tavern), "В замке должна быть таверна");
        assertTrue(buildings.stream().anyMatch(b -> b instanceof GuardPost), "В замке должен быть пост охраны");
    }

    @Test
    @Order(4)
    @DisplayName("Тест получения списка сохранений игрока")
    void testGetPlayerSaves() {
        List<String> saves = saveManager.getAvailableSaves(TEST_PLAYER);
        assertNotNull(saves);
        assertFalse(saves.isEmpty());
        assertTrue(saves.stream().anyMatch(save -> save.startsWith("auto_") || save.startsWith("save_")));
    }


} 