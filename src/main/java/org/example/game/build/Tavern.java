package org.example.game.build;

import org.example.game.Player;
import org.example.game.person.Hero;
import org.example.game.person.Team;

import java.util.HashMap;
import java.util.Map;

public class Tavern extends Building {
    private static final Map<Integer, Integer> heroPrices = new HashMap<>();

    static {
        // Инициализация цен для каждого героя
        heroPrices.put(1, 200);
        heroPrices.put(2, 250);
        heroPrices.put(3, 300);

    }

    public Tavern() {
        super("Таверна", false);
    }

    public static boolean buyHero(int heroChoice, Hero hero, Player player) {
        int priceOfHero = heroPrices.getOrDefault(heroChoice, -1);
        if (priceOfHero == -1) {
            System.out.println("Некорректный выбор героя.");
            return false;
        }
        if (player.hasEnoughGold(priceOfHero)) {
            player.spendGold(priceOfHero);

            // Обновляем переданный объект героя
            switch (heroChoice) {
                case 1 -> {
                    hero.setName("Герой 1");
                    hero.setMaxMoves(15);
                    hero.setTeam(Team.HERO);
                    hero.setGold(1000);
                    hero.setHealth(1000);
                    hero.setAttack(10);
                    hero.setDefense(10);
                    hero.setAttackRange(3);
                }
                case 2 -> {
                    hero.setName("Герой 2");
                    hero.setMaxMoves(15);
                    hero.setTeam(Team.HERO);
                    hero.setGold(1000);
                    hero.setHealth(1000);
                    hero.setAttack(10);
                    hero.setDefense(10);
                    hero.setAttackRange(1);
                }
                case 3 -> {
                    hero.setName("Герой 3");
                    hero.setMaxMoves(15);
                    hero.setTeam(Team.HERO);
                    hero.setGold(1000);
                    hero.setHealth(1000);
                    hero.setAttack(10);
                    hero.setDefense(10);
                    hero.setAttackRange(1);
                }
                default -> {
                    System.out.println("Некорректный выбор героя.");
                    return false;
                }
            }

            System.out.println("Вы успешно купили героя: " + hero.getName() + " за " + priceOfHero + " золота!");
            return true;
        } else {
            System.out.println("У вас недостаточно золота для покупки героя.");
            return false;
        }
    }
    public static void showTavernInfo() {
        System.out.println("Добро пожаловать в таверну!");
        for (Map.Entry<Integer, Integer> entry : heroPrices.entrySet()) {
            System.out.println("Герой " + entry.getKey() + ": Цена - " + entry.getValue() + " золота.");
        }
    }
}