package org.example.game.build;
import org.example.game.person.Hero;
import org.example.game.person.Unit;
import org.example.game.person.Unit.UnitType;

public class GuardPost extends Building {
    private static final int SPEARMAN_COST = 50; // Стоимость копейщика

    public GuardPost() {
        super("Сторожевой пост", true);
    }

    public void buyUnit(Hero hero) {
        if (!canRecruitUnits()) {
            System.out.println(getName() + " не может нанимать юнитов.");
            return;
        }

        if (hero.getGold() < SPEARMAN_COST) {
            System.out.println("Недостаточно золота для найма копейщика.");
            return;
        }

        Unit spearman = new Unit(UnitType.SPEARMAN, 100, 15, 3, 1, hero.getTeam(), hero.getPosition(), "S");
        hero.addUnit(spearman); // Используем метод добавления юнита
        hero.setGold(hero.getGold() - SPEARMAN_COST);

        System.out.println("Герой " + hero.getName() + " нанял копейщика. Остаток золота: " + hero.getGold());
    }
}
