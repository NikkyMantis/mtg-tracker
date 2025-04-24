package com.nikkyev00.mtg_tracker.model;

import lombok.Data;

@Data
public class Card {
    private Integer multiverseId;
    private String name;
    private String type;
    private String rarity;
    private String manaCost;
    private String imageUrl;
    
    // Getters and Setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRarity() {
        return rarity;
    }

    public void setRarity(String rarity) {
        this.rarity = rarity;
    }

    public String getManaCost() {
        return manaCost;
    }

    public void setManaCost(String manaCost) {
        this.manaCost = manaCost;
    }
}
