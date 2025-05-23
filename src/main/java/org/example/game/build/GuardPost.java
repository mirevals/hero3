package org.example.game.build;
import org.example.game.Player;
import org.example.game.person.Hero;
import org.example.game.person.Team;
import org.example.game.person.Unit;

import java.util.ArrayList;
import java.util.List;

public class GuardPost extends Building {

    public GuardPost() {
        super("Сторожевой пост", true);
    }

    // Метод для вывода доступных юнитов с номерами и стоимостью
    public static void displayAvailableUnits(List<Unit> buyUnit) {
        System.out.println("Доступные юниты для найма:");
        for (int i = 0; i < buyUnit.size(); i++) {
            Unit unit = buyUnit.get(i);
            System.out.println(i + 1 + ". " + unit.getType() + " - Стоимость: " + unit.getCost());
        }
    }

    public static boolean buyUnit(int heroChoice, Hero hero, Player player, List<Unit> buyUnit) {
        // Проверка на корректность выбора
        if (heroChoice < 1 || heroChoice > buyUnit.size()) {
            System.out.println("Некорректный выбор.");
            return false;
        }
        Unit selectedUnit = buyUnit.get(heroChoice - 1); // Получаем выбранного юнита
        int unitCost = selectedUnit.getCost();

        // Проверяем, достаточно ли у игрока монет для покупки
        if (player.getGold() < unitCost) {
            System.out.println("Недостаточно монет для покупки юнита.");
            return false;
        }

        // Списываем монеты
        player.spendGold(unitCost);
        System.out.println("Вы купили юнита: " + selectedUnit.getType() + ". Стоимость: " + unitCost + " монет.");

        // Добавляем юнита в список юнитов героя
        hero.addUnit(selectedUnit);
        System.out.println(selectedUnit.getType() + " добавлен в армию героя.");

        return true; // Покупка прошла успешно
    }
}
