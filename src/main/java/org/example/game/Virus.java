package org.example.game;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.example.game.map.Position;

import java.io.IOException;
import java.io.Serializable;

public class Virus implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private boolean attachedToPlayer;
    
    @JsonIgnore // Don't serialize position in JSON
    private Position position;
    
    // Add position coordinates for JSON serialization
    private int positionX;
    private int positionY;

    public Virus() {
        // Default constructor for Jackson
    }

    public Virus(String id, String name) {
        this.id = id;
        this.name = name;
        this.attachedToPlayer = false;
    }

    public Virus(String id, String name, Position position) {
        this.id = id;
        this.name = name;
        this.attachedToPlayer = false;
        this.position = position;
        if (position != null) {
            this.positionX = position.getX();
            this.positionY = position.getY();
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAttachedToPlayer() {
        return attachedToPlayer;
    }

    public void setAttachedToPlayer(boolean attachedToPlayer) {
        this.attachedToPlayer = attachedToPlayer;
    }

    public void attachToPlayer(AccountManager accountManager, String playerName) {
        this.attachedToPlayer = true;
        accountManager.infectAccount(playerName, this);
    }


    public Position getPosition() {
        if (position == null && (positionX != 0 || positionY != 0)) {
            position = new Position(positionX, positionY);
        }
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
        if (position != null) {
            this.positionX = position.getX();
            this.positionY = position.getY();
        }
    }

    // Getters and setters for JSON serialization
    public int getPositionX() {
        return position != null ? position.getX() : positionX;
    }

    public void setPositionX(int x) {
        this.positionX = x;
        if (position != null) {
            position = new Position(x, position.getY());
        }
    }

    public int getPositionY() {
        return position != null ? position.getY() : positionY;
    }

    public void setPositionY(int y) {
        this.positionY = y;
        if (position != null) {
            position = new Position(position.getX(), y);
        }
    }

    // Method to serialize Virus object to JSON
    public String toJson() {
        try {
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            return ow.writeValueAsString(this);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Static method to deserialize JSON to Virus object
    public static Virus fromJson(String json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Virus virus = objectMapper.readValue(json, Virus.class);
            if (virus.positionX != 0 || virus.positionY != 0) {
                virus.position = new Position(virus.positionX, virus.positionY);
            }
            return virus;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
} 