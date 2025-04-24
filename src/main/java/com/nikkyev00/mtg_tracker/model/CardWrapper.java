package com.nikkyev00.mtg_tracker.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Wrapper for the API response at https://api.magicthegathering.io/v1/cards/{multiverseId}
 */
public class CardWrapper {

    @JsonProperty("card")
    private Card card;

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }
}