package com.nikkyev00.mtg_tracker.model;

import jakarta.persistence.*;

@Entity
@Table(name = "collection_items")
public class CollectionItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String cardId;

    public CollectionItem() {
    }

    public CollectionItem(String username, String cardId) {
        this.username = username;
        this.cardId = cardId;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getCardId() {
        return cardId;
    }
}
