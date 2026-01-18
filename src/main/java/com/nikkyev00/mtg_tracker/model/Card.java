package com.nikkyev00.mtg_tracker.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Card {

    /** Printing-specific Scryfall ID */
    private String id;

    /** Shared ID across all printings (Sol Ring concept) */
    @JsonProperty("oracle_id")
    private String oracleId;

    private String name;

    @JsonProperty("type_line")
    private String type;

    private String rarity;

    @JsonProperty("mana_cost")
    private String manaCost;

    /** Set code (e.g. "pfall", "cmm") */
    private String set;

    /** Human-readable set name */
    @JsonProperty("set_name")
    private String setName;

    /** Collector number within the set */
    @JsonProperty("collector_number")
    private String collectorNumber;

    /** Pricing info (usd, usd_foil, etc.) */
    private Map<String, String> prices;

    /** Scryfall-provided URI to fetch all printings of this card */
    @JsonProperty("prints_search_uri")
    private String printsSearchUri;

    /* -----------------------------
       Image handling
       ----------------------------- */

    @JsonProperty("image_uris")
    private ImageUris imageUris;

    @JsonProperty("card_faces")
    private List<CardFace> cardFaces;

    /**
     * Used by Thymeleaf: ${card.imageUrl}
     * Safe for single- and double-faced cards
     */
    public String getImageUrl() {
        if (imageUris != null) {
            if (imageUris.small != null) return imageUris.small;
            if (imageUris.normal != null) return imageUris.normal;
            if (imageUris.large != null) return imageUris.large;
        }

        if (cardFaces != null && !cardFaces.isEmpty()) {
            ImageUris faceUris = cardFaces.get(0).imageUris;
            if (faceUris != null) {
                if (faceUris.small != null) return faceUris.small;
                if (faceUris.normal != null) return faceUris.normal;
                if (faceUris.large != null) return faceUris.large;
            }
        }

        return null;
    }

    /* -----------------------------
       Nested helper classes
       ----------------------------- */

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ImageUris {
        public String small;
        public String normal;
        public String large;
    }

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CardFace {
        @JsonProperty("image_uris")
        private ImageUris imageUris;
    }
}
