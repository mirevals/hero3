package org.example.game;

import org.example.account.Account;
import org.example.account.AccountManager;
import org.example.game.map.GameMap;
import org.example.game.map.MapManager;
import org.example.game.map.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Game {
    private int turnCount;
    private List<Virus> activeVirusesOnMap;
    private AccountManager accountManager;
    private GameMap gameMap;
    private MapManager mapManager;
    private static final int VIRUS_SPAWN_INTERVAL = 5; // Virus appears every 5 turns
    private static final double VIRUS_SPREAD_CHANCE = 0.3; // 30% chance to spread virus when moving
    private Random random = new Random();

    public Game(AccountManager accountManager, GameMap gameMap) {
        this.turnCount = 0;
        this.activeVirusesOnMap = new ArrayList<>();
        this.accountManager = accountManager;
        this.gameMap = gameMap;
    }

    public void setMapManager(MapManager mapManager) {
        this.mapManager = mapManager;
    }

    public void nextTurn() {
        turnCount++;
        System.out.println("--- Turn " + turnCount + " ---");
        spawnVirusIfNeeded();
        // Simulate player actions
        // In a real game, this would involve player input or AI

        // Display the map after each turn to show virus presence
    }

    private void spawnVirusIfNeeded() {
        if (turnCount % VIRUS_SPAWN_INTERVAL == 0) {
            String virusId = "virus_" + turnCount;
            String virusName = "Virus-" + turnCount;
            Random random = new Random();
            // Find a random empty position on the map
            int x, y;
            do {
                x = random.nextInt(gameMap.getWidth());
                y = random.nextInt(gameMap.getHeight());
            } while (gameMap.getCellValue(x, y) != ' '); // Assuming ' ' is an empty cell

            Position virusPosition = new Position(x, y);
            Virus newVirus = new Virus(virusId, virusName, virusPosition);
            activeVirusesOnMap.add(newVirus);
            gameMap.setCellValue(x, y, 'V'); // Place 'V' on the map
            System.out.println("A new virus '" + newVirus.getName() + "' appeared at (" + x + ", " + y + ")!");
            // mapManager.displayMap(); // Map is displayed in nextTurn()
        }
    }

    public void playerPicksUpVirus(Virus virus) {
        Account currentAccount = accountManager.getCurrentAccount().orElse(null);
        if (currentAccount != null && activeVirusesOnMap.contains(virus)) {
            currentAccount.addVirus(virus);
            currentAccount.setInfected(true);
            activeVirusesOnMap.remove(virus);
            // Remove 'V' from the map after pickup
            if (virus.getPosition() != null) {
                gameMap.setCellValue(virus.getPosition().getX(), virus.getPosition().getY(), ' ');
            }
            System.out.println(currentAccount.getUsername() + " picked up virus: " + virus.getName());
            // mapManager.displayMap(); // Map is displayed in nextTurn()
        } else {
            System.out.println("Virus not found on the map or no active account.");
        }
    }

    public void playerDropsVirus(Virus virus) {
        Account currentAccount = accountManager.getCurrentAccount().orElse(null);
        if (currentAccount != null && currentAccount.getViruses().contains(virus)) {
            currentAccount.removeVirus(virus);
            // If all viruses are dropped, perhaps the account is no longer infected
            if (!currentAccount.hasVirus()) {
                currentAccount.setInfected(false);
            }
            System.out.println(currentAccount.getUsername() + " dropped virus: " + virus.getName() + " on another map.");
            // For now, we don't place it back on the map since it's dropped on "another map"
        } else {
            System.out.println("Player does not have this virus attached or no active account.");
        }
    }

    public List<Virus> getActiveVirusesOnMap() {
        return activeVirusesOnMap;
    }

    public int getTurnCount() {
        return turnCount;
    }

    public void handleMapTransition(String newMapName) {
        Account currentAccount = accountManager.getCurrentAccount().orElse(null);
        if (currentAccount != null) {
            // Always update infection state from account
            boolean isInfected = currentAccount.isInfected();
            List<Virus> accountViruses = currentAccount.getViruses();
            
            System.out.println("\n=== Состояние аккаунта при переходе на карту " + newMapName + " ===");
            System.out.println("Заражен: " + isInfected);
            System.out.println("Количество вирусов: " + accountViruses.size());
            
            if (isInfected) {
                System.out.println("\n⚠️ ВНИМАНИЕ: Ваш аккаунт заражен вирусом! ⚠️");
                System.out.println("Герой будет оставлять вирусы на карте при движении.");
                for (Virus virus : accountViruses) {
                    System.out.println("  - Вирус: " + virus.getName() + " (ID: " + virus.getId() + ")");
                }
            }
            
            // Clear any existing viruses on the map when transitioning
            activeVirusesOnMap.clear();
            
            // Update map manager's game state if it exists
            if (mapManager != null) {
                mapManager.updateInfectionState();
            }
        }
    }

    public void handleHeroMovement(Position oldPosition, Position newPosition) {
        Account currentAccount = accountManager.getCurrentAccount().orElse(null);
        if (currentAccount != null && currentAccount.isInfected()) {
            List<Virus> accountViruses = currentAccount.getViruses();
            if (!accountViruses.isEmpty()) {
                System.out.println("Account has " + accountViruses.size() + " viruses");
                // Chance to spread virus at the old position
                if (random.nextDouble() < VIRUS_SPREAD_CHANCE) {
                    // Get a random virus from the account to spread
                    Virus virusToSpread = accountViruses.get(random.nextInt(accountViruses.size()));
                    System.out.println("Attempting to spread virus: " + virusToSpread.getName());
                    
                    // Create a new virus instance for the map
                    String virusId = "virus_" + System.currentTimeMillis();
                    String virusName = "Spread-" + virusToSpread.getName();
                    Virus spreadVirus = new Virus(virusId, virusName, oldPosition);
                    
                    // Check if position is valid and empty
                    if (gameMap.getCellValue(oldPosition.getX(), oldPosition.getY()) == ' ') {
                        activeVirusesOnMap.add(spreadVirus);
                        gameMap.setCellValue(oldPosition.getX(), oldPosition.getY(), 'V');
                        System.out.println("Герой оставил вирус '" + virusName + "' на позиции (" + oldPosition.getX() + ", " + oldPosition.getY() + ")!");
                    } else {
                        System.out.println("Не удалось оставить вирус на позиции (" + oldPosition.getX() + ", " + oldPosition.getY() + ") - клетка занята");
                    }
                } else {
                    System.out.println("Не удалось распространить вирус (шанс не выпал)");
                }
            } else {
                System.out.println("У аккаунта нет вирусов для распространения");
            }
        } else {
            System.out.println("Аккаунт не заражен или не найден");
        }
    }

    public AccountManager getAccountManager() {
        return accountManager;
    }
}