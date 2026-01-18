package com.nikkyev00.mtg_tracker.model;

import lombok.Data;

@Data
public class OwnedPrinting {

    private String scryfallId;
    private String setCode;
    private String setName;
    private String collectorNumber;

    private int quantity = 1;

    public OwnedPrinting(String scryfallId,
                         String setCode,
                         String setName,
                         String collectorNumber) {
        this.scryfallId = scryfallId;
        this.setCode = setCode;
        this.setName = setName;
        this.collectorNumber = collectorNumber;
    }

    public void incrementQuantity() {
        quantity++;
    }
}
