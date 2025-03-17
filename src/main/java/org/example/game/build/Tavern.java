package org.example.game.build;

import org.example.game.Player;
import org.example.game.person.Hero;
import org.example.game.person.Team;

import java.util.HashMap;
import java.util.Map;

public class Tavern extends Building  {
    private static final Map<Integer, Integer> heroPrices = new HashMap<>();  // Карта с ценами для каждого героя
    // Инициализация цен для каждого героя
    static {
        // Инициализация цен для каждого героя
        heroPrices.put(1, 200);  // Цена для героя 1
        heroPrices.put(2, 250);  // Цена для героя 2
        heroPrices.put(3, 300);  // Цена для героя 3
    }

    public Tavern(Player player) {
        super("Таверна", false);

    }



    public static boolean buyHero(int heroChoice, Player player) {
        int priceOfHero = heroPrices.getOrDefault(heroChoice, -1);

        if (priceOfHero == -1) {
            System.out.println("Некорректный выбор героя.");
            return false;
        }

        if (player.hasEnoughGold(priceOfHero)) {
            player.spendGold(priceOfHero);
            System.out.println("Вы успешно купили героя за " + priceOfHero + " золота!");
            return true;  // Покупка успешна
        } else {
            System.out.println("У вас недостаточно золота для покупки героя.");
            return false;  // Покупка не удалась
        }
    }

    // Метод для создания героя
    public static Hero createHero(int heroChoice, int mapWidth, int mapHeight) {
        switch (heroChoice) {
            case 1:
                return new Hero("Герой 1", 15, Team.HERO, 1000, mapWidth, mapHeight);
            case 2:
                return new Hero("Герой 2", 14, Team.HERO, 800, mapWidth, mapHeight);
            case 3:
                return new Hero("Герой 3", 16, Team.HERO, 1200, mapWidth, mapHeight);
            default:
                System.out.println("Некорректный выбор героя.");
                return null;
        }
    }

    public static void showTavernInfo() {
        System.out.println("Добро пожаловать в таверну!");
        for (int i = 1; i <= 3; i++) {
            int price = heroPrices.get(i);
            System.out.println("Герой " + i + ": Цена - " + price + " золота.");
        }
    }
}