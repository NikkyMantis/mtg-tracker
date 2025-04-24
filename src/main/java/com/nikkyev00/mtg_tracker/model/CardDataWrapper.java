package com.nikkyev00.mtg_tracker.model;

import lombok.Data;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
@Data
public class CardDataWrapper {/**
     * The list of cards returned by the MTG API.
     */
    @JsonProperty("cards")
    private List<Card> cards;

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }
}
