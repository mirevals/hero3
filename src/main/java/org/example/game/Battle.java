package org.example.game;

import java.util.List;

public class Battle {
    private BattleField battleField;
    private List<Unit> units;

    public Battle(BattleField battleField) {
        this.battleField = battleField;
        this.units = battleField.getUnits();
    }

    public boolean autoFight() {
        boolean heroWon = false;

        while (true) {
            boolean hasHeroes = false;
            boolean hasEnemies = false;

            for (Unit unit : units) {
                if (unit.getHealth() > 0) {
                    Unit enemy = findNearestEnemy(unit);

                    if (enemy != null) {
                        if (unit.getTeam() == Team.HERO) hasHeroes = true;
                        else hasEnemies = true;

                        double distance = getDistance(unit, enemy);

                        if (distance <= unit.getAttackRange()) {
                            unit.attack(enemy);
                        } else if (distance <= unit.getMoveRange()) {
                            moveUnitTowardsEnemy(unit, enemy);
                        }
                    }
                }
            }

            battleField.printField();

            if (!hasHeroes) {
                System.out.println("Враги победили!");
                return false;
            }
            if (!hasEnemies) {
                System.out.println("Герои победили!");
                heroWon = true;
                break;
            }
        }

        return heroWon;
    }

    private Unit findNearestEnemy(Unit unit) {
        Unit nearestEnemy = null;
        double minDistance = Double.MAX_VALUE;

        for (Unit target : units) {
            if (target.getTeam() != unit.getTeam() && target.getHealth() > 0) {
                double distance = getDistance(unit, target);
                if (distance < minDistance) {
                    minDistance = distance;
                    nearestEnemy = target;
                }
            }
        }
        return nearestEnemy;
    }

    private double getDistance(Unit unit1, Unit unit2) {
        int dx = unit2.getPosition().getX() - unit1.getPosition().getX();
        int dy = unit2.getPosition().getY() - unit1.getPosition().getY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    private void moveUnitTowardsEnemy(Unit unit, Unit enemy) {
        Position enemyPos = enemy.getPosition();
        Position unitPos = unit.getPosition();

        int dx = Integer.compare(enemyPos.getX(), unitPos.getX());
        int dy = Integer.compare(enemyPos.getY(), unitPos.getY());

        int newX = unitPos.getX() + dx;
        int newY = unitPos.getY() + dy;

        if (battleField.canMoveTo(newX, newY)) {
            unit.setPosition(new Position(newX, newY));
            System.out.println(unit.getName() + " перемещается к врагу.");
        }
    }
}