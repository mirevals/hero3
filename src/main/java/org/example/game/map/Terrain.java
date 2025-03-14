package org.example.game.map;

public class Terrain {

    // Перечисление типов местности
    public enum TerrainType {
        PLAIN,    // Равнина
        FOREST,   // Лес
        MOUNTAIN, // Горы
        ROAD, WATER     // Вода
    }

    private final TerrainType type;
    private final int movementPenalty;  // Штраф к перемещению для этого типа местности

    // Конструктор для создания типа местности с определенным штрафом
    public Terrain(TerrainType type) {
        this.type = type;

        // Устанавливаем штраф в зависимости от типа местности
        if (type == TerrainType.PLAIN) {
            this.movementPenalty = 0;  // Без штрафа
        } else if (type == TerrainType.FOREST) {
            this.movementPenalty = 1;  // Штраф 1
        } else if (type == TerrainType.MOUNTAIN) {
            this.movementPenalty = 2;  // Штраф 2
        } else {
            this.movementPenalty = Integer.MAX_VALUE;  // Невозможно пройти (вода)
        }
    }

    // Геттеры
    public TerrainType getType() {
        return type;
    }

    public int getMovementPenalty() {
        return movementPenalty;
    }

    // Метод для проверки, можно ли пройти через эту местность
    public boolean isWalkable() {
        return movementPenalty != Integer.MAX_VALUE;
    }

    public static String getTerritoryType(int x, int width) {
        // Геройская территория
        if (x < width / 3) {
            return "Геройская территория";
        }
        // Вражеская территория
        else if (x > 2 * width / 3) {
            return "Вражеская территория";
        }
        // Нейтральная территория
        return "Нейтральная территория";
    }

    static void placeObstacles(char[][] map, int width, int height) {
        // Левый участок - собственная территория
        for (int y = 0; y < height; y++) {
            map[y][width / 3] = '#';  // Препятствия
        }

        // Правый участок - территория противника
        for (int y = 0; y < height; y++) {
            map[y][2 * width / 3] = '#';  // Препятствия
        }
    }

}