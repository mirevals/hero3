package org.example.game.build;

import org.example.game.map.Position;

import java.io.Serializable;

public class HeroCastle extends Castle implements Serializable {

    private static final long serialVersionUID = 1L;
    private final Position position;  // Своя позиция для HeroCastle
      // Нестатический список построек

    public HeroCastle(int height, int width) {
        super("Замок героя", new Position(width / 6, height / 4));
        this.position = new Position(width / 6, height / 4); // Своя позиция

    }


    @Override
    public CastleType getType() {
        return CastleType.HERO;
    }

    @Override
    public Position getPosition() {
        return this.position; // Возвращаем именно позицию этого замка
    }



}