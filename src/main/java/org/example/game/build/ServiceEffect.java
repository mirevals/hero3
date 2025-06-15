package org.example.game.build;

import java.io.Serializable;

public class ServiceEffect implements Serializable {
    private static final long serialVersionUID = -4267364649894094910L;

    public enum EffectType {
        HEALTH_BOOST,
        MOVEMENT_BOOST,
        CASTLE_CAPTURE_TIME_REDUCTION
    }

    private EffectType type;
    private int value;

    public ServiceEffect(EffectType type, int value) {
        this.type = type;
        this.value = value;
    }

    public EffectType getType() {
        return type;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        switch (type) {
            case HEALTH_BOOST:
                return String.format("+%d to health", value);
            case MOVEMENT_BOOST:
                return String.format("+%d to movement", value);
            case CASTLE_CAPTURE_TIME_REDUCTION:
                return "Castle capture time reduced to 1 turn";
            default:
                return "Unknown effect";
        }
    }
} 