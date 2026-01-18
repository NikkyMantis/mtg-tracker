package com.nikkyev00.mtg_tracker.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nikkyev00.mtg_tracker.model.Card;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

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

    /* ================= SEARCH ================= */

    public List<Card> search(
            String name,
            String color,
            String type,
            String rarity,
            String matchType
    ) {
        try {
            if (name == null || name.isBlank()) {
                return List.of();
            }

            String encodedQuery =
                    URLEncoder.encode(name.trim(), StandardCharsets.UTF_8);

            String url =
                    "https://api.scryfall.com/cards/search?q=" + encodedQuery;

            System.out.println("SCRYFALL URL = " + url);

            String json = restTemplate.getForObject(url, String.class);

            JsonNode root = objectMapper.readTree(json);
            JsonNode data = root.get("data");

            System.out.println("SCRYFALL data.size() = " + data.size());

            return objectMapper.readValue(
                    data.toString(),
                    new TypeReference<List<Card>>() {}
            );

        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    /* ================= FETCH BY ID ================= */

    public Card getCardById(String cardId) {
        try {
            String url = "https://api.scryfall.com/cards/" + cardId;
            return restTemplate.getForObject(url, Card.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
