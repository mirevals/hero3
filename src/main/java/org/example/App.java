package org.example;

import org.example.battle.BattleField;
import org.example.entities.Hero;
import org.example.entities.Position;
import org.example.entities.buildings.Castle;
import org.example.entities.buildings.Tavern;
import org.example.entities.buildings.Stable;
import org.example.game.Battle;

import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        // Создание замков
        Castle playerCastle = new Castle("Замок Игрока", new Position(0, 0));
        Castle enemyCastle = new Castle("Замок Противника", new Position(9, 9));

        // Создание героев
        Hero playerHero = new Hero("Герой Игрока", 100, 3, playerCastle.getPosition());
        Hero enemyHero = new Hero("Герой Противника", 100, 3, enemyCastle.getPosition());

        // Добавление строений в замки
        Tavern playerTavern = new Tavern("Таверна", 50);
        playerCastle.addBuilding(playerTavern);

        Stable playerStable = new Stable("Конюшня", 100);
        playerCastle.addBuilding(playerStable);

        // Создание поля сражения
        BattleField battleField = new BattleField(10, 10, playerHero, enemyHero);

        // Сканер для ввода команд
        Scanner scanner = new Scanner(System.in);

        while (true) {
            // Отображение поля сражения
            battleField.printField();

            // Отображение героя
            printHero(playerHero);

            // Вывод команд для игрока
            System.out.println("Команды: w (вверх), s (вниз), a (влево), d (вправо), r (нанять героя), q (выйти)");

            String command = scanner.nextLine();

            int x = playerHero.getPosition().getX();
            int y = playerHero.getPosition().getY();

            // Обработка команд для перемещения героя
            switch (command) {
                case "w" -> moveHero(battleField, playerHero, x, y - 1);
                case "s" -> moveHero(battleField, playerHero, x, y + 1);
                case "a" -> moveHero(battleField, playerHero, x - 1, y);
                case "d" -> moveHero(battleField, playerHero, x + 1, y);
                case "r" -> recruitHero(scanner, playerHero, playerCastle, playerTavern);
                case "q" -> {
                    System.out.println("Выход из игры.");
                    return;
                }
                default -> System.out.println("Неверная команда!");
            }

            // Проверка на сражение
            if (playerHero.getPosition().equals(enemyHero.getPosition())) {
                Battle.startBattle(playerHero, enemyHero);
            }
        }
    }

    // Метод для перемещения героя
    private static void moveHero(BattleField battleField, Hero hero, int newX, int newY) {
        Position newPosition = new Position(newX, newY);
        if (battleField.isWalkable(newPosition)) {
            hero.setPosition(newPosition);
        }
    }

    // Метод для найма нового героя
    private static void recruitHero(Scanner scanner, Hero playerHero, Castle playerCastle, Tavern playerTavern) {
        if (playerHero.getPosition().equals(playerCastle.getPosition())) {
            if (playerCastle.canBuildTavern()) {
                // Запрашиваем данные для нового героя
                System.out.println("Введите имя героя:");
                String heroName = scanner.nextLine();

                System.out.println("Введите здоровье героя:");
                int heroHealth = Integer.parseInt(scanner.nextLine());

                System.out.println("Введите урон героя:");
                int heroDamage = Integer.parseInt(scanner.nextLine());

                // Нанимаем нового героя
                Hero newHero = playerTavern.recruitHero(heroName, heroHealth, heroDamage, playerHero.getPosition());
                System.out.println("Нанят герой: " + newHero.getName());
            } else {
                System.out.println("Таверна не построена или не готова к найму.");
            }
        } else {
            System.out.println("Герой должен быть в замке для найма.");
        }
    }

    // Метод для отображения героя в виде ASCII арта
    private static void printHero(Hero hero) {
        System.out.println("Герой: " + hero.getName());
        System.out.println("Здоровье: " + hero.getHealth());
        System.out.println("Урон: " + hero.getDamage());
        System.out.println("Позиция: " + hero.getPosition().getX() + ", " + hero.getPosition().getY());

        // Простой ASCII арт героя
        System.out.println("   O   ");
        System.out.println("  /|\\  ");
        System.out.println("  / \\  ");
        System.out.println("Герой на позиции: " + hero.getPosition().getX() + ", " + hero.getPosition().getY());
    }
}