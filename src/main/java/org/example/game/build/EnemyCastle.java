package org.example.game.build;

import org.example.game.map.Position;

public class EnemyCastle extends Castle{
    public EnemyCastle(int height, int width) {
        super("Замок врага", new Position(5 * width / 6, height / 4));
    }
}
