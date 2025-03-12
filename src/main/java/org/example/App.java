package org.example;

import org.example.game.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        // Создание карты
        int width = 10;
        int height = 10;
        GameMap gameMap = new GameMap(width, height);

        // Создание героя
        Team team = Team.HERO;
        Position startPosition = new Position(gameMap.getHeroX(), gameMap.getHeroY());

        // Создание списка юнитов для героя
        List<Unit> heroUnits = new ArrayList<>();
        heroUnits.add(new Unit(Unit.UnitType.WARRIOR, 100, 10, 5, 3, team, startPosition));

        // Создание героя с передачей параметров
        Hero character = new Hero("Герой", 5, startPosition, heroUnits, Team.HERO);

        // Создание врага
        List<Unit> enemyUnits = new ArrayList<>();
        enemyUnits.add(new Unit(Unit.UnitType.WARRIOR, 100, 10, 5, 3, Team.ENEMY, new Position(gameMap.getEnemyX(), gameMap.getEnemyY())));

        // Создание врага с передачей параметров
        Enemy enemy = new Enemy("Враг", 5, new Position(gameMap.getEnemyX(), gameMap.getEnemyY()), enemyUnits, Team.ENEMY);

        // Печать стартовой карты
        gameMap.printMap();

        Scanner scanner = new Scanner(System.in);

        // Основной цикл игры
        while (true) {
            System.out.println("\nКоманды: ");
            System.out.println("w - вверх");
            System.out.println("s - вниз");
            System.out.println("a - влево");
            System.out.println("d - вправо");
            System.out.println("q - выход");

            System.out.print("Введите команду: ");
            String command = scanner.nextLine();

            switch (command) {
                case "w":
                    gameMap.moveHero(0, -1, character, enemy);  // Перемещение вверх
                    break;
                case "s":
                    gameMap.moveHero(0, 1, character, enemy);   // Перемещение вниз
                    break;
                case "a":
                    gameMap.moveHero(-1, 0, character, enemy);  // Перемещение влево
                    break;
                case "d":
                    gameMap.moveHero(1, 0, character, enemy);   // Перемещение вправо
                    break;
                case "q":
                    System.out.println("Выход из игры.");
                    scanner.close();
                    return;
                default:
                    System.out.println("Неверная команда. Попробуйте снова.");
                    continue;
            }

            // Печать карты после перемещения
            gameMap.printMap();

            // Проверка на победу
            if (gameMap.getHeroX() == gameMap.getEnemyX() && gameMap.getHeroY() == gameMap.getEnemyY()) {
                System.out.println("Герой встретился с врагом! Начинается битва...");
                BattleField battleField = gameMap.enterBattleField(character, enemy);
                battleField.autoFight();
                break;
            }
        }
    }
}