package org.example.game;


public class Battle {

    private Hero playerHero;
    private Hero enemyHero;
    private boolean isBattleOngoing;

    public Battle(Hero playerHero, Hero enemyHero) {
        this.playerHero = playerHero;
        this.enemyHero = enemyHero;
        this.isBattleOngoing = false;
    }

    // Метод для начала сражения
    public void startBattle() {
        // Проверяем, что оба героя могут вступить в сражение (например, они находятся рядом или в одной клетке)
        if (canStartBattle()) {
            isBattleOngoing = true;
            System.out.println("Сражение началось между " + playerHero.getName() + " и " + enemyHero.getName());
            performBattle();
        } else {
            System.out.println("Сражение невозможно начать. Герои должны находиться рядом.");
        }
    }

    // Проверка, что герои могут начать сражение
    private boolean canStartBattle() {
        // Здесь можно добавить логику для проверки расстояния между героями, чтобы они могли встретиться.
        return playerHero.getPosition().equals(enemyHero.getPosition());
    }

    // Метод для выполнения сражения
    private void performBattle() {
        while (isBattleOngoing) {
            // Каждому герою нужно атаковать в сражении
            attack(playerHero, enemyHero);
            if (!isBattleOngoing) break;

            attack(enemyHero, playerHero);
            if (!isBattleOngoing) break;
        }

        // Определение победителя и последствий
        determineWinner();
    }

    private void attack(Hero attacker, Hero defender) {
        if (attacker.getTeam().hasUnits() && defender.getTeam().hasUnits()) {
            // Удаляем всех юнитов противника (потери)
            defender.getTeam().removeAllUnits();
            System.out.println(attacker.getName() + " атакует " + defender.getName() + "!");
        } else {
            // Если у кого-то не осталось юнитов
            isBattleOngoing = false;
        }
    }

    // Метод для определения победителя
    private void determineWinner() {
        if (playerHero.getTeam().hasUnits()) {
            System.out.println(playerHero.getName() + " победил в сражении!");
        } else {
            System.out.println(enemyHero.getName() + " победил в сражении!");
        }
    }

    // Метод для получения состояния сражения
    public boolean isBattleOngoing() {
        return isBattleOngoing;
    }
}