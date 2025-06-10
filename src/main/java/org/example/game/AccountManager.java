package org.example.game;

import org.example.game.save.SaveManager;
import org.example.game.save.GameState;

import java.io.*;
import java.util.*;

public class AccountManager {
    private static final String ACCOUNTS_FILE = "accounts.dat";
    private static final Map<String, Player> accounts = new HashMap<>();
    private final SaveManager saveManager;

    public AccountManager() {
        this.saveManager = new SaveManager();
        loadAccounts();
    }

    private void loadAccounts() {
        File file = new File(ACCOUNTS_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                Map<String, Player> loadedAccounts = (Map<String, Player>) ois.readObject();
                accounts.putAll(loadedAccounts);
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Ошибка при загрузке аккаунтов: " + e.getMessage());
            }
        }
    }

    private void saveAccounts() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ACCOUNTS_FILE))) {
            oos.writeObject(accounts);
        } catch (IOException e) {
            System.out.println("Ошибка при сохранении аккаунтов: " + e.getMessage());
        }
    }

    public Player getOrCreateAccount(String playerName) {
        // Проверяем существующие сохранения
        GameState savedState = saveManager.loadGame(playerName, "default_map");
        if (savedState != null) {
            Player existingPlayer = savedState.getPlayer();
            if (existingPlayer != null) {
                // Обновляем информацию об аккаунте из сохранения
                accounts.put(playerName, existingPlayer);
                saveAccounts();
                return existingPlayer;
            }
        }

        // Если аккаунт существует в памяти, возвращаем его
        if (accounts.containsKey(playerName)) {
            return accounts.get(playerName);
        }

        // Создаем новый аккаунт
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

    public List<String> getAccountList() {
        return new ArrayList<>(accounts.keySet());
    }
} 