package org.example.game;

public class Gold {

    private int amount; // Количество золота
    private int x; // Координата x на карте
    private int y; // Координата y на карте

    // Конструктор, создающий золото с определённым количеством и координатами
    public Gold(int amount, int x, int y) {
        this.amount = amount;
        this.x = x;
        this.y = y;
    }

    // Метод для получения количества золота
    public int getAmount() {
        return amount;
    }

    // Метод для получения координат золота
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    // Метод для забора золота игроком
    public int collectGold() {
        int collectedAmount = this.amount;
        this.amount = 0; // Золото забрано, количество обнуляется
        return collectedAmount;
    }

    // Метод для проверки, находится ли золото в данной клетке
    public boolean isAtPosition(int x, int y) {
        return this.x == x && this.y == y;
    }
}