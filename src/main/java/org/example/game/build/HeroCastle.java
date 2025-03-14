package org.example.game.build;

import org.example.game.map.Position;

public class HeroCastle extends Castle {
    public HeroCastle(int height, int width) {
        super("Замок героя", new Position(height / 4, width / 6));
    }
}