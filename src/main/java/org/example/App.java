package org.example;

import org.example.game.Gold;
import org.example.game.build.CastleManager;
import org.example.game.map.GameMap;
import org.example.game.map.MapManager;
import org.example.game.map.Position;
import org.example.game.person.Enemy;
import org.example.game.person.Hero;
import org.example.game.person.Team;
import org.example.game.person.Unit;

import java.util.ArrayList;
import java.util.List;

import static org.example.game.build.CastleManager.CastleType.HERO;

public class App {
    public static void main(String[] args) {


        CastleManager.enterCastle(10, 10, HERO);
    }
}