package org.example.entities.buildings;

import org.example.entities.Hero;

public class Stable extends Building {
    public Stable(String name, int cost) {
        super(name, cost); // Передаем и название, и стоимость в конструктор базового класса
    }

    @Override
    public void build() {
        System.out.println("Конюшня построена. Все герои увеличат свою дальность перемещения.");
    }

    public void applyEffect(Hero hero) {
        // Увеличиваем дальность перемещения героя
        hero.setMovementRange(hero.getMovementRange() + 1);
        System.out.println(hero.getName() + " теперь имеет дальность перемещения " + hero.getMovementRange());
    }
}