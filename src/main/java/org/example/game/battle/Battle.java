package org.example.game.battle;

import org.example.game.map.Position;
import org.example.game.person.Team;
import org.example.game.person.Unit;

import java.util.List;
import java.util.Random;

public class Battle {
    private static Random random = new Random();

    public static boolean autoFight(BattleField battleField, List<Unit> allUnits) {
        while (true) {
            boolean hasHeroes = processUnits(Team.HERO, allUnits, battleField);
            boolean hasEnemies = processUnits(Team.ENEMY, allUnits, battleField);

            if (!hasHeroes) {
                System.out.println("Враги победили!");
                return false;
            }
            if (!hasEnemies) {
                System.out.println("Герои победили!");
                return true;
            }
        }
    }

    private static boolean processUnits(Team team, List<Unit> allUnits, BattleField battleField) {
        boolean hasUnits = false;
        for (Unit unit : allUnits) {
            if (unit.getTeam() == team && unit.getHealth() > 0) {
                hasUnits = true;
                Unit enemy = findNearestEnemy(unit, allUnits);

                if (enemy != null) {
                    performAction(unit, enemy, battleField);
                    battleField.printField();
                    moveUnit(unit, battleField);
                    battleField.printField();

                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
        return hasUnits;
    }

    private static Unit findNearestEnemy(Unit unit, List<Unit> allUnits) {
        Unit nearestEnemy = null;
        int minDistance = Integer.MAX_VALUE;

        for (Unit otherUnit : allUnits) {
            if (otherUnit.getTeam() != unit.getTeam() && otherUnit.getHealth() > 0) {
                int distance = getManhattanDistance(unit, otherUnit);
                if (distance < minDistance) {
                    minDistance = distance;
                    nearestEnemy = otherUnit;
                }
            }
        }
        return nearestEnemy;
    }

    private static int getManhattanDistance(Unit unit1, Unit unit2) {
        return Math.abs(unit2.getPosition().getX() - unit1.getPosition().getX()) +
                Math.abs(unit2.getPosition().getY() - unit1.getPosition().getY());
    }

    private static void performAction(Unit unit, Unit enemy, BattleField battleField) {
        int distance = getManhattanDistance(unit, enemy);

        if (distance == 1) {
            unit.attack(enemy);
            System.out.println(unit.getName() + " атакует " + enemy.getName());
        } else if (distance > 1) {
            moveUnitTowardsEnemy(unit, enemy, battleField);
        }
    }

    private static void moveUnitTowardsEnemy(Unit unit, Unit enemy, BattleField battleField) {
        Position enemyPos = enemy.getPosition();
        Position unitPos = unit.getPosition();
        int dx = Integer.compare(enemyPos.getX(), unitPos.getX());
        int dy = Integer.compare(enemyPos.getY(), unitPos.getY());

        int newX = unitPos.getX() + dx;
        int newY = unitPos.getY() + dy;

        if (battleField.canMoveTo(newX, newY)) {
            unit.setPosition(new Position(newX, newY));
            System.out.println(unit.getName() + " движется к врагу.");
            battleField.printField();
        }
    }

    private static void moveUnit(Unit unit, BattleField battleField) {
        Position position = unit.getPosition();
        int x = position.getX();
        int y = position.getY();

        int newX = x + random.nextInt(3) - 1;
        int newY = y + random.nextInt(3) - 1;

        if (battleField.canMoveTo(newX, newY)) {
            battleField.setFieldCell(x, y, ' ');
            unit.setPosition(new Position(newX, newY));
            battleField.setFieldCell(newX, newY, unit.getSymbol());
        }
    }
}