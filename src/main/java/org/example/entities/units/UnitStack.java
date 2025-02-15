package org.example.entities.units;

public class UnitStack {
    private final Unit unit;
    private int count;

    public UnitStack(Unit unit, int count) {
        this.unit = unit;
        this.count = count;
    }

    public int getTotalHealth() {
        return unit.getHealth() * count;
    }

    public int getTotalDamage() {
        return unit.getDamage() * count;
    }

    public int getCount() {
        return count;
    }

    public void takeDamage(int damage) {
        int totalHealth = getTotalHealth() - damage;
        count = totalHealth / unit.getHealth();
        if (totalHealth % unit.getHealth() > 0) count++; // Остаток юнита
        if (count <= 0) count = 0;

        System.out.println("Стек " + unit.getName() + " потерял юнитов! Осталось: " + count);
    }

    public boolean isAlive() {
        return count > 0;
    }

    public void attack(UnitStack targetStack) {
        if (this.unit.getTeam() != targetStack.unit.getTeam()) {
            // Атакуем вражеский стек
            targetStack.takeDamage(this.getTotalDamage());
        } else {
            System.out.println("Нельзя атаковать юнитов своей команды!");
        }
    }

    @Override
    public String toString() {
        return unit.getName() + " x" + count + " (Общий ХП: " + getTotalHealth() + ", Общий Урон: " + getTotalDamage() + ")";
    }
}