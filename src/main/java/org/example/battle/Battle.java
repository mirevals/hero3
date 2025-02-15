package org.example.game;

import org.example.entities.Hero;

public class Battle {

    public static void startBattle(Hero playerHero, Hero enemyHero) {
        // Проверяем, если герой игрока и герой противника находятся в одной клетке
        if (playerHero.getPosition().equals(enemyHero.getPosition())) {
            System.out.println("Сражение началось между героями!");

            // Логика сражения между героями
            while (playerHero.isAlive() && enemyHero.isAlive()) {
                // Симуляция атаки
                enemyHero.takeDamage(playerHero.getAttack());
                if (enemyHero.isAlive()) {
                    playerHero.takeDamage(enemyHero.getAttack());
                }
            }

            // После сражения выводим результат
            if (playerHero.isAlive()) {
                System.out.println("Победа игрока!");
                // Проигравший теряет все юниты и героя
                removeAllUnitsAndHero(enemyHero);
            } else {
                System.out.println("Победа противника!");
                removeAllUnitsAndHero(playerHero);
            }
        }
    }

    private static void removeAllUnitsAndHero(Hero defeatedHero) {
        // Удаляем все юниты и героя проигравшего
        if (defeatedHero.isAlive()) {
            System.out.println("Все юниты и герой " + defeatedHero + " уничтожены.");
        }
    }
}