package org.example.game;


public class Battle {

    private Character playerCharacter;
    private Character enemyCharacter;
    private boolean isBattleOngoing;

    public Battle(Character playerCharacter, Character enemyCharacter) {
        this.playerCharacter = playerCharacter;
        this.enemyCharacter = enemyCharacter;
        this.isBattleOngoing = false;
    }

    // Метод для начала сражения
    public void startBattle() {
        // Проверяем, что оба героя могут вступить в сражение (например, они находятся рядом или в одной клетке)
        if (canStartBattle()) {
            isBattleOngoing = true;
            System.out.println("Сражение началось между " + playerCharacter.getName() + " и " + enemyCharacter.getName());
            performBattle();
        } else {
            System.out.println("Сражение невозможно начать. Герои должны находиться рядом.");
        }
    }

    // Проверка, что герои могут начать сражение
    private boolean canStartBattle() {
        // Здесь можно добавить логику для проверки расстояния между героями, чтобы они могли встретиться.
        return playerCharacter.getPosition().equals(enemyCharacter.getPosition());
    }

    // Метод для выполнения сражения
    private void performBattle() {
        while (isBattleOngoing) {
            // Каждому герою нужно атаковать в сражении
            attack(playerCharacter, enemyCharacter);
            if (!isBattleOngoing) break;

            attack(enemyCharacter, playerCharacter);
            if (!isBattleOngoing) break;
        }

        // Определение победителя и последствий
        determineWinner();
    }

    private void attack(Character attacker, Character defender) {
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
        if (playerCharacter.getTeam().hasUnits()) {
            System.out.println(playerCharacter.getName() + " победил в сражении!");
        } else {
            System.out.println(enemyCharacter.getName() + " победил в сражении!");
        }
    }

    // Метод для получения состояния сражения
    public boolean isBattleOngoing() {
        return isBattleOngoing;
    }
}