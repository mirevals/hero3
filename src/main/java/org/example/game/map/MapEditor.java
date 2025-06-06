package org.example.game.map;

import java.util.Scanner;
import java.io.*;
import java.util.List;
import java.util.ArrayList;

public class MapEditor {
    private static final String MAPS_DIRECTORY = "maps";
    private GameMap currentMap;
    private final Scanner scanner;

    public MapEditor() {
        this.scanner = new Scanner(System.in);
        createMapsDirectoryIfNotExists();
    }

    private void createMapsDirectoryIfNotExists() {
        File mapsDir = new File(MAPS_DIRECTORY);
        if (!mapsDir.exists()) {
            mapsDir.mkdir();
        }
    }

    public void start() {
        while (true) {
            System.out.println("\nРедактор карт:");
            System.out.println("1. Создать новую карту");
            System.out.println("2. Загрузить существующую карту");
            System.out.println("3. Удалить карту");
            System.out.println("4. Вернуться в главное меню");
            
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline
            
            switch (choice) {
                case 1:
                    createNewMap();
                    break;
                case 2:
                    loadMap();
                    break;
                case 3:
                    deleteMap();
                    break;
                case 4:
                    return;
                default:
                    System.out.println("Неверный выбор!");
            }
        }
    }

    private void createNewMap() {
        System.out.println("Введите название новой карты:");
        String mapName = scanner.nextLine();
        
        System.out.println("Введите размер карты (ширина высота):");
        int width = scanner.nextInt();
        int height = scanner.nextInt();
        
        currentMap = new GameMap(width, height);
        editMap();
        saveMap(mapName);
    }

    private void editMap() {
        while (true) {
            currentMap.printMap();
            System.out.println("\nРедактирование карты:");
            System.out.println("1. Добавить замок (C)");
            System.out.println("2. Добавить дорогу (R)");
            System.out.println("3. Добавить препятствие (#)");
            System.out.println("4. Добавить равнину (P)");
            System.out.println("5. Добавить лес (F)");
            System.out.println("6. Добавить горы (M)");
            System.out.println("7. Добавить воду (W)");
            System.out.println("8. Очистить клетку");
            System.out.println("9. Сохранить и выйти");
            
            int choice = scanner.nextInt();
            if (choice == 9) break;
            
            System.out.println("Введите координаты (x y):");
            int x = scanner.nextInt();
            int y = scanner.nextInt();
            
            if (x < 0 || x >= currentMap.getWidth() || y < 0 || y >= currentMap.getHeight()) {
                System.out.println("Неверные координаты!");
                continue;
            }
            
            switch (choice) {
                case 1:
                    currentMap.setCellValue(x, y, 'C');
                    break;
                case 2:
                    currentMap.setCellValue(x, y, 'R');
                    break;
                case 3:
                    currentMap.setCellValue(x, y, '#');
                    break;
                case 4:
                    currentMap.setCellValue(x, y, 'P');
                    break;
                case 5:
                    currentMap.setCellValue(x, y, 'F');
                    break;
                case 6:
                    currentMap.setCellValue(x, y, 'M');
                    break;
                case 7:
                    currentMap.setCellValue(x, y, 'W');
                    break;
                case 8:
                    currentMap.setCellValue(x, y, ' ');
                    break;
            }
        }
    }

    private void saveMap(String mapName) {
        try {
            String normalizedMapName = mapName.replace("maps/", "");
            File mapFile = new File(MAPS_DIRECTORY + "/" + normalizedMapName + ".map");
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(mapFile));
            out.writeObject(currentMap);
            out.close();
            System.out.println("Карта успешно сохранена!");
        } catch (IOException e) {
            System.out.println("Ошибка при сохранении карты: " + e.getMessage());
        }
    }

    private void loadMap() {
        List<String> maps = getAvailableMaps();
        if (maps.isEmpty()) {
            System.out.println("Нет доступных карт!");
            return;
        }

        System.out.println("\nДоступные карты:");
        for (int i = 0; i < maps.size(); i++) {
            System.out.println((i + 1) + ". " + maps.get(i));
        }

        System.out.println("Выберите карту (номер):");
        int choice = scanner.nextInt();
        if (choice < 1 || choice > maps.size()) {
            System.out.println("Неверный выбор!");
            return;
        }

        String mapName = maps.get(choice - 1);
        try {
            String normalizedMapName = mapName.replace("maps/", "");
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(MAPS_DIRECTORY + "/" + normalizedMapName));
            currentMap = (GameMap) in.readObject();
            in.close();
            editMap();
            saveMap(normalizedMapName.replace(".map", ""));
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Ошибка при загрузке карты: " + e.getMessage());
        }
    }

    private void deleteMap() {
        List<String> maps = getAvailableMaps();
        if (maps.isEmpty()) {
            System.out.println("Нет доступных карт!");
            return;
        }

        System.out.println("\nДоступные карты:");
        for (int i = 0; i < maps.size(); i++) {
            System.out.println((i + 1) + ". " + maps.get(i));
        }

        System.out.println("Выберите карту для удаления (номер):");
        int choice = scanner.nextInt();
        if (choice < 1 || choice > maps.size()) {
            System.out.println("Неверный выбор!");
            return;
        }

        String mapName = maps.get(choice - 1);
        File mapFile = new File(MAPS_DIRECTORY + "/" + mapName);
        if (mapFile.delete()) {
            System.out.println("Карта успешно удалена!");
        } else {
            System.out.println("Ошибка при удалении карты!");
        }
    }

    public static List<String> getAvailableMaps() {
        List<String> maps = new ArrayList<>();
        File mapsDir = new File(MAPS_DIRECTORY);
        if (mapsDir.exists() && mapsDir.isDirectory()) {
            File[] mapFiles = mapsDir.listFiles((dir, name) -> name.endsWith(".map"));
            if (mapFiles != null) {
                for (File mapFile : mapFiles) {
                    maps.add(mapFile.getName());
                }
            }
        }
        return maps;
    }
} 