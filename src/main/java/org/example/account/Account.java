package org.example.account;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.example.game.person.Hero;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Account {
    private final String id;
    private final String username;
    private String passwordHash;

    private List<Hero> heroes;
    private String activeHeroId;
    private final long createdAt;
    private long lastLoginAt;

    private boolean infected = false;
    private List<org.example.game.Virus> viruses = new ArrayList<>();

    @JsonCreator
    public Account(@JsonProperty("username") String username, @JsonProperty("passwordHash") String passwordHash) {
        this.id = UUID.randomUUID().toString();
        this.username = username;
        this.passwordHash = passwordHash;
        this.heroes = new ArrayList<>();
        this.activeHeroId = null;
        this.createdAt = System.currentTimeMillis();
        this.lastLoginAt = this.createdAt;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public List<Hero> getHeroes() {
        return heroes;
    }

    public void addHero(Hero hero) {
        this.heroes.add(hero);
        if (this.activeHeroId == null) {
            this.activeHeroId = hero.getId();
        }
    }

    public Hero getActiveHero() {
        return heroes.stream()
                .filter(hero -> hero.getId().equals(activeHeroId))
                .findFirst()
                .orElse(null);
    }

    public void setActiveHero(String heroId) {
        if (heroes.stream().anyMatch(hero -> hero.getId().equals(heroId))) {
            this.activeHeroId = heroId;
        }
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getLastLoginAt() {
        return lastLoginAt;
    }

    public void updateLastLogin() {
        this.lastLoginAt = System.currentTimeMillis();
    }

    public boolean isInfected() {
        return infected;
    }

    public void setInfected(boolean infected) {
        System.out.println("Account " + username + " infection state changed to: " + infected);
        this.infected = infected;
    }

    public List<org.example.game.Virus> getViruses() {
        return viruses;
    }

    public void addVirus(org.example.game.Virus virus) {
        System.out.println("Adding virus " + virus.getName() + " to account " + username);
        viruses.add(virus);
        if (!infected) {
            setInfected(true);
        }
    }

    public void removeVirus(org.example.game.Virus virus) {
        System.out.println("Removing virus " + virus.getName() + " from account " + username);
        viruses.remove(virus);
        if (viruses.isEmpty()) {
            setInfected(false);
        }
    }

    public boolean hasVirus() {
        return !viruses.isEmpty();
    }

    @Override
    public String toString() {
        return String.format("Account{id='%s', username='%s', heroes=%d, activeHeroId='%s'}",
            id, username, heroes.size(), activeHeroId);
    }
} 