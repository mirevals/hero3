package org.example.game.time;

public class GameTime {
    private static final long GAME_MINUTE_MS = 100; // 1 game minute = 100ms real time
    private static long gameTimeMs = 0; // Total game time in milliseconds
    
    public static void updateTime(long realTimeMs) {
        gameTimeMs += realTimeMs;
    }
    
    public static long getGameTimeMs() {
        return gameTimeMs;
    }
    
    public static long realTimeToGameMinutes(long realTimeMs) {
        return realTimeMs / GAME_MINUTE_MS;
    }
    
    public static long gameMinutesToRealTime(long gameMinutes) {
        return gameMinutes * GAME_MINUTE_MS;
    }
    
    public static String formatGameTime() {
        long totalMinutes = gameTimeMs / GAME_MINUTE_MS;
        long days = totalMinutes / (24 * 60);
        long hours = (totalMinutes % (24 * 60)) / 60;
        long minutes = totalMinutes % 60;
        
        return String.format("%dд %02d:%02d", days, hours, minutes);
    }
} 