package org.example.game.save;

import org.example.game.Player;
import org.example.game.map.GameMap;
import org.example.game.person.*;
import org.example.game.battle.BattleField;
import org.example.game.build.*;

import java.io.*;
import java.util.List;
import java.util.ArrayList;

public class SaveManager {
    private static final String SAVES_DIRECTORY = "saves";
    private static final String AUTO_SAVE_PREFIX = "auto_";
    private static final String MANUAL_SAVE_PREFIX = "save_";

    public SaveManager() {
        createSavesDirectoryIfNotExists();
    }

    private void createSavesDirectoryIfNotExists() {
        File savesDir = new File(SAVES_DIRECTORY);
        if (!savesDir.exists()) {
            savesDir.mkdir();
        }
    }

    public void saveGame(String playerName, GameState gameState, boolean isAutoSave) {
        try {
            String fileName = generateSaveFileName(playerName, isAutoSave); //Генерация имени файла
            File saveFile = new File(SAVES_DIRECTORY + "/" + fileName); //Создание файла сохранения
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(saveFile));// записваем данные в файл и преобразуем объект в байты
            out.writeObject(gameState); //Сохраняем в в файл
            out.close();
            System.out.println(isAutoSave ? "Игра автоматически сохранена!" : "Игра успешно сохранена!");
        } catch (IOException e) {
            System.out.println("Ошибка при сохранении игры:: " + e.getMessage());
        }
    }

    public GameState loadGame(String playerName, String saveName) {
        try {
            File saveFile = new File(SAVES_DIRECTORY + "/" + saveName);
            if (!saveFile.exists()) {
                System.out.println("Сохранение не найдено!");
                return null;
            }
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(saveFile));
            GameState gameState = (GameState) in.readObject();
            in.close();
            return gameState;
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Ошибка при загрузке игры: " + e.getMessage());
            return null;
        }
    }

    public List<String> getAvailableSaves(String playerName) {
        List<String> saves = new ArrayList<>();
        File savesDir = new File(SAVES_DIRECTORY);
        if (savesDir.exists() && savesDir.isDirectory()) {
            File[] saveFiles = savesDir.listFiles((dir, name) -> 
                (name.startsWith(MANUAL_SAVE_PREFIX + playerName) || 
                 name.startsWith(AUTO_SAVE_PREFIX + playerName)) && 
                name.endsWith(".sav")
            );
            if (saveFiles != null) {
                for (File save : saveFiles) {
                    saves.add(save.getName());
                }
            }
        }
        return saves;
    }

    private String generateSaveFileName(String playerName, boolean isAutoSave) {
        String prefix = isAutoSave ? AUTO_SAVE_PREFIX : MANUAL_SAVE_PREFIX;
        String timestamp = String.valueOf(System.currentTimeMillis());
        return prefix + playerName + "_" + timestamp + ".sav";
    }

    public void deleteSave(String saveName) {
        File saveFile = new File(SAVES_DIRECTORY + "/" + saveName);
        if (saveFile.exists()) {
            if (saveFile.delete()) {
                System.out.println("Сохранение успешно удалено!");
            } else {
                System.out.println("Ошибка при удалении сохранения!");
            }
        }
    }
} 