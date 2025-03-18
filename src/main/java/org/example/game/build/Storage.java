package org.example.game.build;

public class Storage {

    static boolean useBuilding = false;
    // Метод для использования здания на основе выбранного номера здания и типа замка
    public static boolean useBuilding(int buildingChoice, Castle castle) {
        // Псевдокод для обработки выбора здания, зависит от типа замка
        if (castle.getType() == Castle.CastleType.HERO) {
            // Логика использования здания для героев
            useBuilding = true;
            System.out.println("Вы используете здание из списка построек героев.");
            // Здесь можно добавить код, который использует конкретное здание из списка
        } else if (castle.getType() == Castle.CastleType.ENEMY) {
            // Логика использования здания для врагов
            useBuilding = true;
            System.out.println("Вы используете здание из списка построек врагов.");
            // Здесь можно добавить код, который использует конкретное здание из списка

        } else {
            System.out.println("Неизвестный тип замка.");
        }

        // Возвращаем объект Storage или необходимый объект для дальнейшей работы
        return useBuilding;
    }
}