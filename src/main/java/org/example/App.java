package org.example;

import org.example.game.build.Castle;
import org.example.game.build.CastleManager;
import org.example.game.build.HeroCastle;

public class App {
    public static void main(String[] args) {

        // Вход в замок
        CastleManager.enterCastle(Castle.CastleType.HERO);
    }
}