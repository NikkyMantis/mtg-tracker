package com.nikkyev00.mtg_tracker.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor // 🔥 REQUIRED FOR JACKSON
@JsonIgnoreProperties(ignoreUnknown = true)
public class Card {

    private String id;
    private String name;

    @JsonProperty("type_line")
    private String type;

    private String rarity;

    @JsonProperty("mana_cost")
    private String manaCost;

    // Single-faced cards
    @JsonProperty("image_uris")
    private ImageUris imageUris;

    // Double-faced / UB cards
    @JsonProperty("card_faces")
    private List<CardFace> cardFaces;

    /**
     * Used by Thymeleaf: ${card.imageUrl}
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

    @Data
    @NoArgsConstructor // 🔥 REQUIRED
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ImageUris {
        private String small;
        private String normal;
        private String large;
    }

    @Data
    @NoArgsConstructor // 🔥 REQUIRED
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CardFace {
        @JsonProperty("image_uris")
        private ImageUris imageUris;
    }
}
