package org.example.game.build;

import org.example.game.Player;
import org.example.game.person.Hero;
import org.example.game.person.Team;

import java.util.HashMap;
import java.util.Map;

public class Tavern extends Building {
    private static final Map<Integer, Integer> heroPrices = new HashMap<>();
    private static final Map<Integer, Hero> heroes = new HashMap<>();
    private static final Map<Player, Hero> purchasedHeroes = new HashMap<>();

    static {
        // Инициализация цен для каждого героя
        heroPrices.put(1, 200);
        heroPrices.put(2, 250);
        heroPrices.put(3, 300);

        // Инициализация статических героев
        heroes.put(1, new Hero("Герой 1", 15, Team.HERO, 1000, 0, 0));
        heroes.put(2, new Hero("Герой 2", 14, Team.HERO, 800, 0, 0));
        heroes.put(3, new Hero("Герой 3", 16, Team.HERO, 1200, 0, 0));
    }

    public Tavern(Player player) {
        super("Таверна", false);
    }

    public static boolean buyHero(int heroChoice, Player player) {
        if (purchasedHeroes.containsKey(player)) {
            System.out.println("Вы уже купили героя: " + purchasedHeroes.get(player).getName());
            return false;
        }

        int priceOfHero = heroPrices.getOrDefault(heroChoice, -1);
        if (priceOfHero == -1) {
            System.out.println("Некорректный выбор героя.");
            return false;
        }
        if (player.hasEnoughGold(priceOfHero)) {
            player.spendGold(priceOfHero);
            Hero hero = heroes.get(heroChoice);
            purchasedHeroes.put(player, hero);
            System.out.println("Вы успешно купили героя: " + hero.getName() + " за " + priceOfHero + " золота!");
            return true;
        } else {
            System.out.println("У вас недостаточно золота для покупки героя.");
            return false;
        }
    }

    public static Hero getHero(Player player) {
        return purchasedHeroes.get(player);
    }

    public static void showTavernInfo() {
        System.out.println("Добро пожаловать в таверну!");
        for (Map.Entry<Integer, Integer> entry : heroPrices.entrySet()) {
            System.out.println("Герой " + entry.getKey() + ": Цена - " + entry.getValue() + " золота.");
        }
    }
}