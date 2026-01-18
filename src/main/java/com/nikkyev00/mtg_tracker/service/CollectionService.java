package com.nikkyev00.mtg_tracker.service;

import com.nikkyev00.mtg_tracker.model.Card;
import com.nikkyev00.mtg_tracker.model.OracleCard;
import com.nikkyev00.mtg_tracker.model.OwnedPrinting;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CollectionService {

    /**
     * username -> oracleId -> OracleCard
     */
    private final Map<String, Map<String, OracleCard>> userCollections = new HashMap<>();

    public void addToCollection(String username, Card card) {
        userCollections.putIfAbsent(username, new HashMap<>());
        Map<String, OracleCard> collection = userCollections.get(username);

        OracleCard oracleCard = collection.get(card.getOracleId());

        if (oracleCard == null) {
            oracleCard = new OracleCard(
                    card.getOracleId(),
                    card.getName(),
                    card.getType(),
                    card.getManaCost()
            );
            collection.put(card.getOracleId(), oracleCard);
        }

        OwnedPrinting printing = new OwnedPrinting(
                card.getId(),
                card.getSet(),
                card.getSetName(),
                card.getCollectorNumber()
        );

        oracleCard.addOrIncrementPrinting(printing);
    }

    public Collection<OracleCard> getUserCollection(String username) {
        return userCollections
                .getOrDefault(username, Collections.emptyMap())
                .values();
    }

    public void removePrinting(String username, String scryfallId) {
        Map<String, OracleCard> collection = userCollections.get(username);
        if (collection == null) return;

        for (OracleCard oracleCard : collection.values()) {
            oracleCard.getPrintings()
                    .removeIf(p -> p.getScryfallId().equals(scryfallId));
        }
    }
}
