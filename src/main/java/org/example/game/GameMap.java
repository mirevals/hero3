package org.example.game;

import org.example.entities.Hero;
import org.example.entities.Position;
import org.example.entities.units.Unit;
import org.example.entities.units.Team;

public class GameMap {
    private final int width;
    private final int height;
    private final Cell[][] map;
    private final Hero playerHero;
    private final Hero enemyHero;

    // Позиции замков
    private final Position playerCastle;
    private final Position enemyCastle;

    public GameMap(int width, int height) {
        this.width = width;
        this.height = height;
        this.map = new Cell[height][width];

        // Инициализация карты с препятствиями и дорогами
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (x < width / 3) {
                    // Область игрока
                    map[y][x] = new Cell(CellType.TERRITORY_PLAYER, 1);
                } else if (x < 2 * width / 3) {
                    // Нейтральная область
                    map[y][x] = new Cell(CellType.TERRITORY_NEUTRAL, 3);
                } else {
                    // Область компьютера
                    map[y][x] = new Cell(CellType.TERRITORY_ENEMY, 1);
                }
            }
        }

        // Добавляем препятствия, разделяющие области
        for (int y = 0; y < height; y++) {
            map[y][width / 3] = new Cell(CellType.OBSTACLE, 0);
            map[y][2 * width / 3] = new Cell(CellType.OBSTACLE, 0);
        }

        // Добавляем дорогу
        for (int y = height / 4; y < 3 * height / 4; y++) {
            map[y][width / 3 + 1] = new Cell(CellType.ROAD, 1);
            map[y][2 * width / 3 - 1] = new Cell(CellType.ROAD, 1);
        }

        // Позиции замков
        playerCastle = new Position(0, height / 2);  // Замок игрока
        enemyCastle = new Position(width - 1, height / 2);  // Замок противника

        // Создаем команды
        Team playerTeam = new Team("Игрок");
        Team enemyTeam = new Team("Компьютер");

        // Размещение героя игрока и компьютера
        this.playerHero = new Hero("Игрок", 100, 50, playerCastle);
        this.enemyHero = new Hero("Компьютер", 100, 50, enemyCastle);

        // Добавляем юнитов в армию обоих героев
        playerHero.addUnit(new Unit("Солдат", 50, 20, 3, 2, playerTeam));
        enemyHero.addUnit(new Unit("Солдат", 50, 20, 3, 2, enemyTeam));
    }

    public Hero getPlayerHero() {
        return playerHero;
    }

    public Hero getEnemyHero() {
        return enemyHero;
    }

    // Метод для проверки доступности клетки
    public boolean isWalkable(Position position) {
        Cell cell = map[position.getY()][position.getX()];
        return cell.getType() != CellType.OBSTACLE;  // Проверка на препятствия
    }

    // Метод для проверки, что перемещение между замками занимает минимум 2 хода
    public boolean isMinTwoMoves(Position from, Position to) {
        // Проверяем, если игрок двигается от замка до замка
        if (from.equals(playerCastle) && to.equals(enemyCastle)) {
            return true;  // Перемещение от замка игрока к замку компьютера
        } else if (from.equals(enemyCastle) && to.equals(playerCastle)) {
            return true;  // Перемещение от замка компьютера к замку игрока
        }
        return false;
    }

    // Метод для отображения карты
    public void printMap() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                char displayChar = '.';

                // Печать различных типов клеток
                if (map[y][x].getType() == CellType.OBSTACLE) {
                    displayChar = '#';
                } else if (map[y][x].getType() == CellType.ROAD) {
                    displayChar = '=';
                } else if (map[y][x].getType() == CellType.TERRITORY_PLAYER) {
                    displayChar = 'P';
                } else if (map[y][x].getType() == CellType.TERRITORY_ENEMY) {
                    displayChar = 'E';
                } else if (map[y][x].getType() == CellType.TERRITORY_NEUTRAL) {
                    displayChar = 'N';
                }

                // Отображение героев
                if (new Position(x, y).equals(playerHero.getPosition())) {
                    displayChar = 'H'; // 'H' для героя игрока
                } else if (new Position(x, y).equals(enemyHero.getPosition())) {
                    displayChar = 'C'; // 'C' для героя противника
                }

                System.out.print(displayChar + " ");
            }
            System.out.println();
        }
    }
}