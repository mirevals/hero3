package org.example.game.map;

import java.io.*;

public class MapSaveManager {
    private static final String MAPS_DIRECTORY = "maps/";

    public MapSaveManager() {
        // Создаем директорию для карт при инициализации, если её нет
        File mapsDir = new File(MAPS_DIRECTORY);
        if (!mapsDir.exists()) {
            mapsDir.mkdirs();
        }
    }

    public boolean saveMap(GameMap map, String mapName) {
        if (map == null) {
            throw new IllegalArgumentException("Карта не может быть null");
        }
        if (mapName == null || mapName.trim().isEmpty()) {
            throw new IllegalArgumentException("Имя файла не может быть пустым");
        }
        if (mapName.contains("/") || mapName.contains("\\")) {
            throw new IllegalArgumentException("Имя файла содержит недопустимые символы");
        }

        String fullPath = MAPS_DIRECTORY + mapName;
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fullPath))) {
            out.writeObject(map);
            return true;
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении карты: " + e.getMessage());
            return false;
        }
    }

    public GameMap loadMap(String mapName) {
        if (mapName == null || mapName.trim().isEmpty()) {
            throw new IllegalArgumentException("Имя файла не может быть пустым");
        }

        String fullPath = MAPS_DIRECTORY + mapName;
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(fullPath))) {
            return (GameMap) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Ошибка при загрузке карты: " + e.getMessage());
            return null;
        }
    }

    public boolean deleteMap(String mapName) {
        if (mapName == null || mapName.trim().isEmpty()) {
            throw new IllegalArgumentException("Имя файла не может быть пустым");
        }

        File mapFile = new File(MAPS_DIRECTORY + mapName);
        return mapFile.exists() && mapFile.delete();
    }

    public String[] getAvailableMaps() {
        File mapsDir = new File(MAPS_DIRECTORY);
        return mapsDir.list((dir, name) -> name.endsWith(".map"));
    }
} 