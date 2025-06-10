package org.example.game;

import org.example.account.Account;
import org.example.account.AccountManager;
import org.example.game.map.GameMap;
import org.example.game.map.MapManager;
import org.example.game.map.Position;
import org.example.game.person.Carriage;
import org.example.game.person.Enemy;
import org.example.game.person.Hero;
import org.example.game.person.Team;
import org.example.game.map.Road;
import org.example.game.build.EnemyCastle;
import org.example.game.build.HeroCastle;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

class VirusInfectionTest {
    private AccountManager accountManager;
    private Account account;
    private Hero hero;
    private Virus virus;
    private Game game;
    private GameMap gameMap;
    private MapManager mapManager;
    private final String username = "testuser";
    private final String password = "testpass";

    @BeforeEach
    void setUp() {
        accountManager = new AccountManager();
        accountManager.register(username, password);
        accountManager.login(username, password);
        account = accountManager.getCurrentAccount().orElseThrow(() -> new IllegalStateException("No active account found"));
        hero = new Hero("TestHero", 5, Team.HERO, 100, 10, 10, 100, 10, 10, 1, new java.util.ArrayList<>());
        account.addHero(hero);
        gameMap = new GameMap(10, 10);

        // Create dummy game entities for MapManager constructor
        HeroCastle heroCastle = new HeroCastle(gameMap.getHeight(), gameMap.getWidth());
        EnemyCastle enemyCastle = new EnemyCastle(gameMap.getHeight(), gameMap.getWidth());
        Enemy enemy = new Enemy("TestEnemy", 5, Team.ENEMY, 50, 10, 10, 50, 8, 3, 2, new java.util.ArrayList<>());
        Road road = new Road(0, 0, 0, 0);
        Carriage carriage = new Carriage(new Position(0, 0), 1, 5, Carriage.Direction.RIGHT);

        mapManager = new MapManager(heroCastle, enemyCastle, enemy, hero, gameMap, road, carriage);
        game = new Game(accountManager, gameMap, mapManager);
    }

    @Test
    void testVirusAppearsAndInfectsAccount() {
        // Simulate turns until a virus appears
        while (game.getActiveVirusesOnMap().isEmpty()) {
            game.nextTurn();
        }

        // Pick up the virus
        virus = game.getActiveVirusesOnMap().get(0);
        game.playerPicksUpVirus(virus);

        // Assert that the account now has the virus and is infected
        assertTrue(account.getViruses().contains(virus), "Account should have the virus attached");
        assertTrue(account.isInfected(), "Account should be infected after picking up virus");
    }

    @Test
    void testInfectedAccountSpreadsVirusToOtherMaps() {
        // Simulate turns until a virus appears and is picked up
        while (game.getActiveVirusesOnMap().isEmpty()) {
            game.nextTurn();
        }
        virus = game.getActiveVirusesOnMap().get(0);
        game.playerPicksUpVirus(virus);

        // Player drops the virus on another map
        game.playerDropsVirus(virus);

        // Assert that the account no longer has the virus and is not infected if no other viruses are present
        assertFalse(account.getViruses().contains(virus), "Account should not have the virus attached after dropping");
        assertFalse(account.hasVirus(), "Account should not have any viruses after dropping the last one");
        assertFalse(account.isInfected(), "Account should not be infected after dropping all viruses");
    }
} 