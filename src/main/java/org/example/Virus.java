package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.io.IOException;

public class Virus {
    private String id;
    private String name;
    private boolean attachedToPlayer;

    public Virus() {
        // Default constructor for Jackson
    }

    public Virus(String id, String name) {
        this.id = id;
        this.name = name;
        this.attachedToPlayer = false;
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

    // Static method to deserialize JSON string to Virus object
    public static Virus fromJson(String jsonString) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonString, Virus.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
} 