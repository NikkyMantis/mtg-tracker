package com.nikkyev00.mtg_tracker.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Card {
    private String id;
    private String name;

    @JsonProperty("type_line")
    private String type;

    private String rarity;

    @JsonProperty("mana_cost")
    private String manaCost;

    @JsonProperty("image_uris")
    private ImageUris imageUris;

    /**
     * Helper for Thymeleaf to get a single image URL.
     * Tries small → normal → large.
     */
    public String getImageUrl() {
        if (imageUris == null) {
            return null;
        }
        if (imageUris.getSmall() != null) {
            return imageUris.getSmall();
        }
        if (imageUris.getNormal() != null) {
            return imageUris.getNormal();
        }
        if (imageUris.getLarge() != null) {
            return imageUris.getLarge();
        }
        return null;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ImageUris {
        private String small;
        private String normal;
        private String large;
    }
}