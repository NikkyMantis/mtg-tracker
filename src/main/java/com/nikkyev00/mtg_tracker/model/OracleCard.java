package com.nikkyev00.mtg_tracker.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class OracleCard {

    private String oracleId;
    private String name;
    private String type;
    private String manaCost;

    private List<OwnedPrinting> printings = new ArrayList<>();

    public OracleCard(String oracleId, String name, String type, String manaCost) {
        this.oracleId = oracleId;
        this.name = name;
        this.type = type;
        this.manaCost = manaCost;
    }

    public void addOrIncrementPrinting(OwnedPrinting newPrinting) {
        for (OwnedPrinting printing : printings) {
            if (printing.getScryfallId().equals(newPrinting.getScryfallId())) {
                printing.incrementQuantity();
                return;
            }
        }
        printings.add(newPrinting);
    }
}
