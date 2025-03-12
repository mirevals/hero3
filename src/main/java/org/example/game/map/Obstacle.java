package org.example.game.map;

public class Obstacle {

    // Конструктор без параметров (можно добавить параметры, если потребуется, например, тип препятствия)
    public Obstacle() {
        // Пока не требуется дополнительной логики
    }

    // Метод для проверки, является ли клетка препятствием
    public boolean isImpassable() {
        return true;  // Все объекты Obstacle блокируют путь
    }
}