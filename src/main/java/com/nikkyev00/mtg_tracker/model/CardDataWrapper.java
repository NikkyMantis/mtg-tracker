package com.nikkyev00.mtg_tracker.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CardDataWrapper {

    /**
     * The list of cards returned by Scryfall in a search.
     */
    @JsonProperty("data")
    private List<Card> cards;

    /**
     * True if there are additional pages of results.
     */
    @JsonProperty("has_more")
    private boolean hasMore;

    /**
     * URL for the next page of results (only valid if hasMore is true).
     */
    @JsonProperty("next_page")
    private String nextPage;
}
