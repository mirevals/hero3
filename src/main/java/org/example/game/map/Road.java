package org.example.game.map;

public class Road extends Terrain {

    // Конструктор, который инициализирует тип местности как дорогу
    public Road() {
        super(TerrainType.ROAD);  // Вызов конструктора родительского класса Terrain с типом ROAD
    }

    // Метод для получения штрафа при перемещении (для дороги он минимальный)
    @Override
    public int getMovementPenalty() {
        return 0;  // Дорога имеет минимальный штраф к перемещению
    }

    // Метод для проверки, является ли местность проходимой (дорога всегда проходима)
    @Override
    public boolean isWalkable() {
        return true;  // Дорога проходима
    }
}