package org.example.game.battle;

import org.example.game.map.Position;
import org.example.game.person.Team;
import org.example.game.person.Unit;

import java.util.List;
import java.util.Random;

public class Battle {

    private static Random random = new Random(); // Генератор случайных чисел

    public static boolean autoFight(BattleField battleField, List<Unit> allUnits) {
        boolean heroWon = false;

        while (true) {
            boolean hasHeroes = false;
            boolean hasEnemies = false;

            // Выполнение действий для героев
            hasHeroes = processUnits(Team.HERO, allUnits, battleField);

            // Выполнение действий для врагов
            hasEnemies = processUnits(Team.ENEMY, allUnits, battleField);

            // Проверяем, есть ли живые герои и враги
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

    private static boolean processUnits(Team team, List<Unit> allUnits, BattleField battleField) {
        boolean hasUnits = false;
        for (Unit unit : allUnits) {
            if (unit.getTeam() == team && unit.getHealth() > 0) {
                hasUnits = true;
                Unit enemy = findNearestEnemy(unit, allUnits);

                if (enemy != null) {
                    performAction(unit, enemy, battleField); // Выполнение действия
                    battleField.printField(); // Обновление карты после действия юнита
                    checkAdjacentCells(unit, battleField, allUnits); // Проверка соседних клеток

                    moveUnit(unit, battleField); // Перемещение юнита сразу после действия
                    battleField.printField(); // Обновление карты после перемещения

                    try {
                        Thread.sleep(1); // Задержка 1.5 секунды между ходами
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return hasUnits;
    }

    private static void checkAdjacentCells(Unit unit, BattleField battleField, List<Unit> allUnits) {
        Position position = unit.getPosition();
        int x = position.getX();
        int y = position.getY();

        // Проверка клеток вокруг юнита (включая диагонали)
        char[] adjacentCells = new char[8];
        adjacentCells[0] = battleField.getCell(x - 1, y - 1); // Верхний левый угол
        adjacentCells[1] = battleField.getCell(x, y - 1);     // Верхняя клетка
        adjacentCells[2] = battleField.getCell(x + 1, y - 1); // Верхний правый угол
        adjacentCells[3] = battleField.getCell(x - 1, y);     // Левая клетка
        adjacentCells[4] = battleField.getCell(x + 1, y);     // Правая клетка
        adjacentCells[5] = battleField.getCell(x - 1, y + 1); // Нижний левый угол
        adjacentCells[6] = battleField.getCell(x, y + 1);     // Нижняя клетка
        adjacentCells[7] = battleField.getCell(x + 1, y + 1); // Нижний правый угол

        // Печать символов соседних клеток
        System.out.println("Соседние клетки для юнита " + unit.getName() + ":");
        for (char cell : adjacentCells) {
            System.out.print(cell + " ");
        }
        System.out.println();

        // Логика для взаимодействия с соседними клетками
        for (char cell : adjacentCells) {
            if (unit.getTeam() == Team.HERO) {
                if (cell == 'A') { // Символ для врагов
                    System.out.println("На соседней клетке враг!");
                    // Логика для атаки врага
                    Unit enemy = findUnitAtPosition(unit.getPosition(), 'A', allUnits);
                    if (enemy != null) {
                        enemy.takeDamage(100);  // Атакуем врага
                    }
                } else if (cell == 'W') { // Символ для союзников
                    System.out.println("На соседней клетке союзник.");
                    // Логика для помощи союзнику
                    System.out.println(unit.getName() + " помогает союзнику!");
                    // Можно добавить логику восстановления здоровья или другие действия
                }
            } else if (unit.getTeam() == Team.ENEMY) {
                if (cell == 'W') { // Символ для героев
                    System.out.println("На соседней клетке враг!");
                    // Логика для атаки героя
                    Unit hero = findUnitAtPosition(unit.getPosition(), 'A', allUnits);
                    if (hero != null) {
                        hero.takeDamage(100);  // Атакуем героя
                    }
                } else if (cell == 'A') { // Символ для союзников
                    System.out.println("На соседней клетке союзник.");
                    // Логика для помощи союзнику
                    System.out.println(unit.getName() + " помогает союзнику!");
                    // Можно добавить логику помощи союзнику
                }
            }
        }
    }

    private static Unit findUnitAtPosition(Position position, char symbol, List<Unit> allUnits) {
        // Метод для поиска юнита по позиции и символу на поле
        for (Unit unit : allUnits) {
            if (unit.getPosition().getX() == position.getX() && unit.getPosition().getY() == position.getY()
                    && unit.getSymbol() == symbol) {
                return unit;
            }
        }
        return null;
    }

    // Метод для нахождения ближайшего вражеского юнита
    private static Unit findNearestEnemy(Unit unit, List<Unit> allUnits) {
        Unit nearestEnemy = null;
        int minDistance = Integer.MAX_VALUE;

        for (Unit otherUnit : allUnits) {
            if (otherUnit.getTeam() != unit.getTeam() && otherUnit.getHealth() > 0) {
                int distance = Math.abs(unit.getPosition().getX() - otherUnit.getPosition().getX()) +
                        Math.abs(unit.getPosition().getY() - otherUnit.getPosition().getY());

                if (distance < minDistance) {
                    minDistance = distance;
                    nearestEnemy = otherUnit;
                }
            }
        }

        return nearestEnemy;
    }

    private static double getDistance(Unit unit1, Unit unit2) {
        int dx = unit2.getPosition().getX() - unit1.getPosition().getX();
        int dy = unit2.getPosition().getY() - unit1.getPosition().getY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    private static int getManhattanDistance(Unit unit1, Unit unit2) {
        int dx = Math.abs(unit2.getPosition().getX() - unit1.getPosition().getX());
        int dy = Math.abs(unit2.getPosition().getY() - unit1.getPosition().getY());
        return dx + dy;
    }

    private static void performAction(Unit unit, Unit enemy, BattleField battleField) {
        int distance = getManhattanDistance(unit, enemy); // Используем манхэттенское расстояние

        // Если юнит и враг находятся на одной клетке (атака)
        if (distance == 0) {
            unit.attack(enemy);  // Атакуем
            System.out.println(unit.getName() + " атакует " + enemy.getName());
        }
        // Если враг в пределах диапазона движения или атаки, двигаемся к нему
        else if (distance <= unit.getMoveRange() && distance > 0) {
            moveUnitTowardsEnemy(unit, enemy, battleField); // Двигаемся к врагу
        }
        // Если враг на соседней клетке (позволяет атаковать)
        else if (distance == 1) {
            unit.attack(enemy);  // Атакуем
            System.out.println(unit.getName() + " атакует " + enemy.getName());
        }
    }

    private static void moveUnitTowardsEnemy(Unit unit, Unit enemy, BattleField battleField) {
        Position enemyPos = enemy.getPosition();
        Position unitPos = unit.getPosition();

        // Вычисляем направление, в котором должен двигаться юнит
        int dx = Integer.compare(enemyPos.getX(), unitPos.getX());
        int dy = Integer.compare(enemyPos.getY(), unitPos.getY());

        int newX = unitPos.getX() + dx;
        int newY = unitPos.getY() + dy;

        // Проверяем, можно ли двигаться в эту клетку
        if (battleField.canMoveTo(newX, newY)) {
            unit.setPosition(new Position(newX, newY));
            System.out.println(unit.getName() + " перемещается к врагу.");
            battleField.printField();  // Обновление карты после перемещения
        } else {
            // Если перемещение в предложенную позицию невозможно, пытаемся двигаться в другом направлении
            if (battleField.canMoveTo(unitPos.getX(), newY)) {
                unit.setPosition(new Position(unitPos.getX(), newY));
                System.out.println(unit.getName() + " перемещается по оси Y.");
                battleField.printField();  // Обновление карты
            } else if (battleField.canMoveTo(newX, unitPos.getY())) {
                unit.setPosition(new Position(newX, unitPos.getY()));
                System.out.println(unit.getName() + " перемещается по оси X.");
                battleField.printField();  // Обновление карты
            } else {
                // Если все попытки неудачны, значит, юнит не может двигаться
                System.out.println(unit.getName() + " не может двигаться.");
            }
        }
    }

    // Метод для перемещения юнита
    private static void moveUnit(Unit unit, BattleField battleField) {
        Position position = unit.getPosition();
        int x = position.getX();
        int y = position.getY();

        // Логируем текущую позицию юнита
        System.out.println(unit.getName() + " находится на позиции (" + x + ", " + y + ")");

        // Пробуем переместить юнита в соседнюю клетку
        int newX = x + random.nextInt(3) - 1; // Случайное изменение X (от -1 до 1)
        int newY = y + random.nextInt(3) - 1; // Случайное изменение Y (от -1 до 1)

        // Логируем попытку перемещения
        System.out.println(unit.getName() + " пытается переместиться на позицию (" + newX + ", " + newY + ")");

        // Проверяем, можно ли двигаться в эту клетку
        if (battleField.canMoveTo(newX, newY)) {
            // Логируем успешное перемещение
            System.out.println(unit.getName() + " может переместиться на позицию (" + newX + ", " + newY + ")");

            // Очистить старую позицию
            battleField.setFieldCell(x, y, ' ');
            unit.setPosition(new Position(newX, newY)); // Обновить позицию юнита
            battleField.setFieldCell(newX, newY, unit.getSymbol()); // Установить новый символ

            // Логируем перемещение
            System.out.println(unit.getName() + " перемещен на позицию (" + newX + ", " + newY + ")");
        } else {
            // Логируем, если перемещение невозможно
            System.out.println(unit.getName() + " не может переместиться на позицию (" + newX + ", " + newY + ")");
        }
    }
}