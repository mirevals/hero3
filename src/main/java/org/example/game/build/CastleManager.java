package org.example.game.build;

import org.example.game.person.Character;
import org.example.game.map.GameMap;

import java.util.Scanner;

public class CastleManager {

    private final GameMap gameMap;  // Ссылка на объект GameMap
    public static boolean isInCastle = false;  // Флаг, находится ли герой в замке

    public CastleManager(GameMap gameMap) {
        this.gameMap = gameMap;
    }

    // Метод для обработки команд в замке
    public void processCastleCommands(Character character) {
        // Цикл для нахождения в замке
        while (isInCastle) {
            System.out.println("Вы в замке. Доступные команды:");
            System.out.println("q - выйти из замка");
            System.out.println("h - помощь (список команд)");
            System.out.println("v - взаимодействовать с NPC");

            // Ввод команды
            Scanner scanner = new Scanner(System.in);
            String command = scanner.nextLine().trim().toLowerCase();

            switch (command) {
                case "q":
                    gameMap.exitCastle(character);  // Выход из замка через GameMap
                    // После выхода из замка печатаем карту и завершаем цикл
                    gameMap.printMap();
                    return;  // Прерываем цикл, так как мы больше не в замке
                case "h":
                    showHelp();  // Показать список команд
                    break;
                case "v":
                    interactWithNPC();  // Взаимодействие с NPC
                    break;
                default:
                    System.out.println("Неизвестная команда. Введите 'h' для справки.");
            }
        }
    }

    // Показать помощь по командам
    private void showHelp() {
        System.out.println("Команды:");
        System.out.println("q - выйти из замка");
        System.out.println("h - показать список команд");
        System.out.println("v - взаимодействовать с NPC");
    }

    // Взаимодействие с NPC
    private void interactWithNPC() {
        System.out.println("Вы встретили NPC!");
        System.out.println("Что вы хотите сделать?");
        System.out.println("1 - Поговорить с NPC");
        System.out.println("2 - Купить что-то у NPC");
        System.out.println("3 - Выйти из разговора");

        Scanner scanner = new Scanner(System.in);
        int npcChoice = scanner.nextInt();

        switch (npcChoice) {
            case 1:
                System.out.println("Вы поговорили с NPC.");
                break;
            case 2:
                System.out.println("Вы купили что-то у NPC.");
                break;
            case 3:
                System.out.println("Вы покинули разговор.");
                break;
            default:
                System.out.println("Некорректный выбор.");
        }
    }

    // Включаем замок, флаг для того, чтобы начать обработку команд
    public static void enterCastle(Character character, GameMap gameMap) {
        // Создаем объект CastleManager
        CastleManager castleManager = new CastleManager(gameMap);

        // Устанавливаем флаг нахождения в замке
        castleManager.isInCastle = true;
        System.out.println("Вы вошли в замок!");

        // Запуск обработки команд
        castleManager.processCastleCommands(character);
    }
}