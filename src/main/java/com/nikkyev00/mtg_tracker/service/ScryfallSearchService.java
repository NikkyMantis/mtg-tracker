package com.nikkyev00.mtg_tracker.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nikkyev00.mtg_tracker.model.Card;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ScryfallSearchService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ScryfallSearchService() {
        restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().set(
                    HttpHeaders.USER_AGENT,
                    "MTGTracker/1.0 (+https://github.com/nikkyev00/mtg-tracker)"
            );
            request.getHeaders().set(HttpHeaders.ACCEPT, "application/json");
            return execution.execute(request, body);
        });
    }

    /* =========================
       SEARCH BY NAME
       ========================= */
    public java.util.List<Card> search(
            String name,
            String color,
            String type,
            String rarity,
            String matchType
    ) {
        try {
            if (name == null || name.isBlank()) {
                return java.util.List.of();
            }

            String url = "https://api.scryfall.com/cards/search?q="
                    + java.net.URLEncoder.encode(name.trim(), java.nio.charset.StandardCharsets.UTF_8);

            String json = restTemplate.getForObject(url, String.class);

            var root = objectMapper.readTree(json);
            var data = root.get("data");

            return objectMapper.readValue(
                    data.toString(),
                    new com.fasterxml.jackson.core.type.TypeReference<>() {}
            );

        } catch (Exception e) {
            e.printStackTrace();
            return java.util.List.of();
        }
    }

    /* =========================
       FETCH CARD BY ID (NEW)
       ========================= */
    public Card getCardById(String cardId) {
        try {
            String url = "https://api.scryfall.com/cards/" + cardId;
            String json = restTemplate.getForObject(url, String.class);
            return objectMapper.readValue(json, Card.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
