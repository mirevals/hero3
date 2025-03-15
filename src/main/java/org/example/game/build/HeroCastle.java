package org.example.game.build;

import org.example.game.map.Position;

public class HeroCastle extends Castle {

    private final Position position;  // Своя позиция для HeroCastle


    public HeroCastle(int height, int width) {
        super("Замок героя", new Position(width / 6, height / 4));
        this.position = new Position(width / 6, height / 4); // Своя позиция

    }
    @Override
    public String getType() {
        return "Героический замок";  // Возвращаем строку с типом замка
    }

    @Override
    public Position getPosition() {
        return this.position; // Возвращаем именно позицию этого замка
    }


}