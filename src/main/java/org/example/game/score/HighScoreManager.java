package org.example.game.score;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HighScoreManager {
    private static final String SCORES_FILE = "scores.dat";
    private static final int MAX_SCORES = 5;
    private List<Record> highScores;

    public HighScoreManager() {
        highScores = new ArrayList<>();
        loadScores();
    }

    public void addScore(Record record) {
        highScores.add(record);
        // Сортируем по убыванию очков
        Collections.sort(highScores, Comparator.comparingInt(Record::getScore).reversed());
        
        // Оставляем только TOP MAX_SCORES записей
        if (highScores.size() > MAX_SCORES) {
            highScores = highScores.subList(0, MAX_SCORES);
        }
        
        saveScores();
    }

    public List<Record> getHighScores() {
        return new ArrayList<>(highScores);
    }

    public void displayHighScores() {
        if (highScores.isEmpty()) {
            System.out.println("Рекордов пока нет!");
            return;
        }

        System.out.println("\n=== ТАБЛИЦА РЕКОРДОВ ===");
        System.out.println("Топ " + MAX_SCORES + " игроков:\n");
        
        for (int i = 0; i < highScores.size(); i++) {
            System.out.println((i + 1) + ". " + highScores.get(i));
            System.out.println("----------------------------------------");
        }
    }

    public void displayMapHighScores(String mapName) {
        if (highScores.isEmpty()) {
            System.out.println("Рекордов пока нет!");
            return;
        }

        System.out.println("\n=== РЕКОРДЫ НА КАРТЕ " + mapName + " ===\n");
        boolean found = false;
        int count = 1;
        
        for (Record record : highScores) {
            if (record.getMapName().equalsIgnoreCase(mapName)) {
                System.out.println(count + ". " + record);
                System.out.println("----------------------------------------");
                found = true;
                count++;
            }
        }
        
        if (!found) {
            System.out.println("На этой карте пока нет рекордов!");
        }
    }

    public void displayPlayerHighScores(String playerName) {
        if (highScores.isEmpty()) {
            System.out.println("Рекордов пока нет!");
            return;
        }

        boolean found = false;
        int count = 1;
        
        for (Record record : highScores) {
            if (record.getPlayerName().equalsIgnoreCase(playerName)) {
                System.out.println(count + ". " + record);
                System.out.println("----------------------------------------");
                found = true;
                count++;
            }
        }
        
        if (!found) {
            System.out.println("У игрока " + playerName + " пока нет рекордов!");
        }
    }

    private void saveScores() {
        try (ObjectOutputStream out = new ObjectOutputStream(
                new FileOutputStream(SCORES_FILE))) {
            out.writeObject(highScores);
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении рекордов: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void loadScores() {
        if (!new File(SCORES_FILE).exists()) {
            return;
        }

        try (ObjectInputStream in = new ObjectInputStream(
                new FileInputStream(SCORES_FILE))) {
            highScores = (List<Record>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Ошибка при загрузке рекордов: " + e.getMessage());
            highScores = new ArrayList<>();
        }
    }
} 