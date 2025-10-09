package org.example.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.example.game.Virus;
import org.example.game.person.Hero;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AccountManager {
    private static final String ACCOUNTS_FILE = "accounts.json";
    private static final Path ACCOUNTS_PATH = Paths.get(ACCOUNTS_FILE);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    private Map<String, Account> accounts;
    private Account currentAccount;

    public AccountManager() {
        this.accounts = loadAccounts();
        this.currentAccount = null;
    }

    private Map<String, Account> loadAccounts() {
        try {
            if (Files.exists(ACCOUNTS_PATH)) {
                String json = Files.readString(ACCOUNTS_PATH);
                return objectMapper.readValue(json, new TypeReference<Map<String, Account>>(){});
            }
        } catch (IOException e) {
            System.err.println("Error loading accounts: " + e.getMessage());
        }
        return new HashMap<>();
    }

    private void saveAccounts() {
        try {
            String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(accounts);
            Files.writeString(ACCOUNTS_PATH, json);
        } catch (IOException e) {
            System.err.println("Error saving accounts: " + e.getMessage());
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    public boolean addVirusToCurrentAccount(Virus virus) {
        if (currentAccount != null) {
            currentAccount.addVirus(virus);
            saveAccounts(); // сразу сохраняем
            return true;
        }
        return false;
    }

    public boolean removeVirusFromCurrentAccount(Virus virus) {
        if (currentAccount != null) {
            currentAccount.removeVirus(virus);
            saveAccounts(); // тоже сохраняем
            return true;
        }
        return false;
    }

    public boolean register(String username, String password) {
        if (accounts.containsKey(username)) {
            return false;
        }

        String passwordHash = hashPassword(password);
        Account account = new Account(username, passwordHash);
        accounts.put(username, account);
        saveAccounts();
        return true;
    }

    public boolean login(String username, String password) {
        Account account = accounts.get(username);
        if (account == null) {
            return false;
        }

        String passwordHash = hashPassword(password);
        if (account.getPasswordHash().equals(passwordHash)) {
            currentAccount = account;
            account.updateLastLogin();
            saveAccounts();
            return true;
        }
        return false;
    }

    public void logout() {
        if (currentAccount != null) {
            saveAccounts();
            currentAccount = null;
        }
    }

    public Optional<Account> getCurrentAccount() {
        return Optional.ofNullable(currentAccount);
    }

    public boolean addHeroToCurrentAccount(Hero hero) {
        if (currentAccount != null) {
            currentAccount.addHero(hero);
            saveAccounts();
            return true;
        }
        return false;
    }

    public Optional<Hero> getCurrentHero() {
        return getCurrentAccount()
                .map(Account::getActiveHero);
    }

    public boolean setCurrentHero(String heroId) {
        if (currentAccount != null) {
            currentAccount.setActiveHero(heroId);
            saveAccounts();
            return true;
        }
        return false;
    }

    public boolean changePassword(String currentPassword, String newPassword) {
        if (currentAccount == null) {
            return false;
        }

        String currentHash = hashPassword(currentPassword);
        if (currentAccount.getPasswordHash().equals(currentHash)) {
            currentAccount.setPasswordHash(hashPassword(newPassword));
            saveAccounts();
            return true;
        }
        return false;
    }
} 