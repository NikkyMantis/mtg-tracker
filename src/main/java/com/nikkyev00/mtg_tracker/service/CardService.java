package com.nikkyev00.mtg_tracker.service;

import com.nikkyev00.mtg_tracker.model.Card;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CardService {

    private static final String SCRYFALL_SEARCH_URL =
            "https://api.scryfall.com/cards/search";

    private static final String SCRYFALL_CARD_BY_ID =
            "https://api.scryfall.com/cards/";

    private final RestTemplate restTemplate = new RestTemplate();

    /* ================= SEARCH ================= */

    public List<Card> searchCards(String name,
                                  String color,
                                  String type,
                                  String rarity) {

        String query = buildQuery(name, color, type, rarity);
        String url = SCRYFALL_SEARCH_URL + "?q=" + query;

        Map response = restTemplate.getForObject(url, Map.class);
        List<Map<String, Object>> data =
                (List<Map<String, Object>>) response.get("data");

        List<Card> cards = new ArrayList<>();

        for (Map<String, Object> cardData : data) {
            Card card = restTemplate.getForObject(
                    SCRYFALL_CARD_BY_ID + cardData.get("id"),
                    Card.class
            );
            if (card != null) {
                cards.add(card);
            }
        }

        return cards;
    }

    /* ================= PRINTINGS ================= */

    public List<Card> getPrintingsByCardId(String oracleId) {

        // IMPORTANT: do NOT encode the entire query
        String query = "oracleid:" + oracleId + "&unique=prints";
        String url = SCRYFALL_SEARCH_URL + "?q=" + query;

        Map response = restTemplate.getForObject(url, Map.class);
        List<Map<String, Object>> data =
                (List<Map<String, Object>>) response.get("data");

        List<Card> printings = new ArrayList<>();

        for (Map<String, Object> cardData : data) {
            Card card = restTemplate.getForObject(
                    SCRYFALL_CARD_BY_ID + cardData.get("id"),
                    Card.class
            );
            if (card != null) {
                printings.add(card);
            }
        }

        return printings;
    }

    /* ================= COLLECTION SUPPORT ================= */

    public Card getCardById(String cardId) {
        return restTemplate.getForObject(
                SCRYFALL_CARD_BY_ID + cardId,
                Card.class
        );
    }

    /* ================= QUERY BUILDER ================= */

    private String buildQuery(String name,
                              String color,
                              String type,
                              String rarity) {

        StringBuilder query = new StringBuilder();

        if (name != null && !name.isBlank()) {
            query.append(name.replace(" ", "+"));
        }
        if (color != null && !color.isBlank()) {
            query.append("+c:").append(color);
        }
        if (type != null && !type.isBlank()) {
            query.append("+t:").append(type);
        }
        if (rarity != null && !rarity.isBlank()) {
            query.append("+r:").append(rarity);
        }

        return query.toString();
    }
}
