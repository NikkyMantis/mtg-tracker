package com.nikkyev00.mtg_tracker.model;

import jakarta.persistence.*;

@Entity
@Table(name = "collection_items")
public class CollectionItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String cardId;

    /* ---------- REQUIRED BY JPA ---------- */
    protected CollectionItem() {
        // JPA only
    }

    /* ---------- YOUR CONSTRUCTOR ---------- */
    public CollectionItem(String username, String cardId) {
        this.username = username;
        this.cardId = cardId;
    }

    /* ---------- GETTERS ---------- */

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
