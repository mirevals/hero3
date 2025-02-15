package org.example.entities.buildings;

import org.example.entities.Hero;
import org.example.entities.Position;

public class Tavern extends Building {
    public Tavern(String name, int cost) {
        super(name, cost); // Теперь передаем и название, и стоимость в конструктор базового класса
    }

    @Override
    public void build() {
        System.out.println("Таверна построена. Теперь можно нанимать героев.");
    }

    // Метод для найма героя
    public Hero recruitHero(String heroName, int health, int attack, Position position) {
        Hero hero = new Hero(heroName, health, attack, position);
        System.out.println(heroName + " нанят в Таверне!");
        return hero;
    }
}