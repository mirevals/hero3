package org.example.game;

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
}