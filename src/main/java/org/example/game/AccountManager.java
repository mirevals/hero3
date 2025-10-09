package org.example.game;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.game.save.SaveManager;
import org.example.game.save.GameState;

import java.io.*;
import java.util.*;

public class AccountManager {
    private static final String ACCOUNTS_FILE = "accounts.json";
    private static final Map<String, Player> accounts = new HashMap<>();
    private final SaveManager saveManager;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AccountManager() {
        this.saveManager = new SaveManager();
        loadAccounts();
    }

    private void loadAccounts() {
        File file = new File(ACCOUNTS_FILE);
        if (file.exists()) {
            try {
                Map<String, Player> loadedAccounts = objectMapper.readValue(
                        file,
                        new TypeReference<Map<String, Player>>() {}
                );
                accounts.clear();
                accounts.putAll(loadedAccounts);
            } catch (IOException e) {
                System.out.println("Ошибка при загрузке аккаунтов: " + e.getMessage());
            }
        }
    }

    private void saveAccounts() {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(ACCOUNTS_FILE), accounts);
        } catch (IOException e) {
            System.out.println("Ошибка при сохранении аккаунтов: " + e.getMessage());
        }
    }

    public Player getOrCreateAccount(String playerName) {
        GameState savedState = saveManager.loadGame(playerName, "default_map");
        if (savedState != null) {
            Player existingPlayer = savedState.getPlayer();
            if (existingPlayer != null) {
                accounts.put(playerName, existingPlayer);
                saveAccounts();
                return existingPlayer;
            }
        }

        if (accounts.containsKey(playerName)) {
            return accounts.get(playerName);
        }

        Player newPlayer = new Player(playerName, 1000);
        accounts.put(playerName, newPlayer);
        saveAccounts();
        return newPlayer;
    }

    public boolean isAccountInfected(String playerName) {
        Player player = accounts.get(playerName);
        if (player != null) {
            return player.isInfected();
        }
        return false;
    }

    public void updateAccount(Player player) {
        accounts.put(player.getName(), player);
        saveAccounts();
    }

    public void infectAccount(String playerName, Virus virus) {
        Player player = accounts.get(playerName);
        if (player != null) {
            player.setInfected(true);
            updateAccount(player); // сразу сохраним в accounts.json
        }
    }

    public List<String> getAccountList() {
        return new ArrayList<>(accounts.keySet());
    }
} 