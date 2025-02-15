package org.example.entities.buildings;

import org.example.entities.Hero;

public class MageTower extends Building {
    private final String name = "Магическая башня";

    // Конструктор
    public MageTower(int cost) {
        super("Магическая башня", cost);  // Вызываем конструктор родительского класса
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void build() {
        System.out.println("Магическая башня построена. Теперь она может усиливать магию героев.");
    }

    public void boostHeroMagic(Hero hero) {
        System.out.println(hero.getName() + " получил усиление магии!");
    }
}