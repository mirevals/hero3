package org.example.game.build;

import org.example.game.map.Position;

public class HeroCastle extends Castle {
    public HeroCastle(int height, int width) {
        super("Замок героя", new Position(width / 6, height / 4));
    }
    @Override
    public String getType() {
        return "Героический замок";  // Возвращаем строку с типом замка
    }
}