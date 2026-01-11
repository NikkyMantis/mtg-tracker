package com.nikkyev00.mtg_tracker.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nikkyev00.mtg_tracker.model.Card;
import com.nikkyev00.mtg_tracker.service.CollectionService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/cards")
public class CardViewController {

    private final RestTemplate restTemplate;
    private final CollectionService collectionService;
    private final ObjectMapper objectMapper;

    public CardViewController(CollectionService collectionService) {
        this.restTemplate = new RestTemplate();
        // Always send a friendly User-Agent (Scryfall rejects some anonymous ones)
        this.restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().set(HttpHeaders.USER_AGENT,
                    "MTGTracker/1.0 (+https://github.com/nikkyev00/mtg-tracker)");
            return execution.execute(request, body);
        });

        this.collectionService = collectionService;
        this.objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @GetMapping({"", "/search", "/search/view"})
    public String searchCardsForView(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String rarity,
            @RequestParam(required = false) String matchType,
            @RequestParam(required = false) Boolean success,
            @RequestParam(required = false) String addedCard,
            Model model
    ) {
        model.addAttribute("success", success);
        model.addAttribute("addedCard", addedCard);

        boolean hasFilter = (name != null && !name.isBlank())
                || (color != null && !color.isBlank())
                || (type != null && !type.isBlank())
                || (rarity != null && !rarity.isBlank());
        model.addAttribute("showSearchOnly", !hasFilter);

        if (!hasFilter) {
            model.addAttribute("cards", Collections.emptyList());
            model.addAttribute("name", name);
            model.addAttribute("color", color);
            model.addAttribute("type", type);
            model.addAttribute("rarity", rarity);
            model.addAttribute("matchType", matchType);
            model.addAttribute("isCollection", false);
            return "cards";
        }

        // 🔹 Build the Scryfall URL dynamically
        String url;
        if ("exact".equalsIgnoreCase(matchType) && name != null && !name.isBlank()) {
            // For exact name match
            url = UriComponentsBuilder.fromHttpUrl("https://api.scryfall.com/cards/named")
                    .queryParam("exact", name.trim())
                    .encode(StandardCharsets.UTF_8)
                    .toUriString();
        } else {
            // Build a full query string for broader search
            StringBuilder queryBuilder = new StringBuilder();
            if (name != null && !name.isEmpty()) queryBuilder.append(name).append(" ");
            if (color != null && !color.isEmpty()) queryBuilder.append("color:").append(color).append(" ");
            if (type != null && !type.isEmpty()) queryBuilder.append("type:").append(type).append(" ");
            if (rarity != null && !rarity.isEmpty()) queryBuilder.append("rarity:").append(rarity).append(" ");
            String rawQuery = queryBuilder.toString().trim();

            // include promos, variants, and extras for Secret Lair cards
            url = UriComponentsBuilder.fromHttpUrl("https://api.scryfall.com/cards/search")
                    .queryParam("q", rawQuery + " include:extras include:promo include:variants is:print lang:any"
                        .replace(" ", "+")
                    )
                    .encode(StandardCharsets.UTF_8)
                    .toUriString();
        }
        // --- Special case: Lara Croft Secret Lair (set:sld) ---
if (name != null && name.toLowerCase().contains("lara")) {
    String specialUrl = UriComponentsBuilder
            .fromHttpUrl("https://api.scryfall.com/cards/search")
            .queryParam("q", "\"Lara Croft, Tomb Raider\" set:sld include:extras include:promo include:variants lang:any")
            .encode(StandardCharsets.UTF_8)
            .toUriString();
    System.out.println("DEBUG :: Special Secret Lair URL: " + specialUrl);

    try {
        String json = restTemplate.getForObject(specialUrl, String.class);
        if (json != null) {
            JsonNode root = objectMapper.readTree(json);
            JsonNode dataNode = root.has("data") ? root.path("data") : objectMapper.createArrayNode().add(root);
            List<Card> specialCards = objectMapper.readValue(
                    dataNode.toString(),
                    new TypeReference<List<Card>>() {}
            );
            model.addAttribute("cards", specialCards);
            model.addAttribute("name", name);
            model.addAttribute("color", color);
            model.addAttribute("type", type);
            model.addAttribute("rarity", rarity);
            model.addAttribute("matchType", matchType);
            model.addAttribute("isCollection", false);
            return "cards"; // ✅ Return immediately after successful match
        }
    } catch (Exception e) {
        System.out.println("DEBUG :: Special case failed: " + e.getMessage());
    }
}
        System.out.println("DEBUG :: Scryfall URL: " + url);

        List<Card> cards;
        try {
            String json = null;

            try {
                // 🔹 First attempt: normal /cards/search or /cards/named
                json = restTemplate.getForObject(url, String.class);
            } catch (HttpClientErrorException.NotFound e) {
                // 🔁 Retry using fuzzy name search if likely a single short name
                if (name != null && !name.isBlank() && name.length() < 30) {
                    String fuzzyUrl = UriComponentsBuilder
                            .fromHttpUrl("https://api.scryfall.com/cards/named")
                            .queryParam("fuzzy", name.trim())
                            .encode(StandardCharsets.UTF_8)
                            .toUriString();
                    System.out.println("DEBUG :: Retrying with fuzzy search URL: " + fuzzyUrl);
                    try {
                        json = restTemplate.getForObject(fuzzyUrl, String.class);
                    } catch (HttpClientErrorException.NotFound inner) {
                        System.out.println("DEBUG :: Fuzzy also 404 - no results found.");
                        json = null;
                    }
                } else {
                    System.out.println("DEBUG :: Skipping fuzzy search for multi-word or generic query.");
                }
            }

            // If both attempts failed
            if (json == null) {
                cards = Collections.emptyList();
            } else {
                System.out.println("DEBUG :: raw JSON length: " + json.length());
                JsonNode root = objectMapper.readTree(json);

                JsonNode dataNode;
                if (root.has("data")) {
                    dataNode = root.path("data");
                } else {
                    // single-card endpoint returns an object, not a list
                    dataNode = objectMapper.createArrayNode().add(root);
                }

                cards = objectMapper.readValue(
                        dataNode.toString(),
                        new TypeReference<List<Card>>() {}
                );
            }

        } catch (Exception e) {
            System.err.println("DEBUG :: Exception caught: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("apiError", "Unexpected error: " + e.getMessage());
            cards = Collections.emptyList();
        }

        // 🔹 Always send back consistent model data
        model.addAttribute("cards", cards);
        model.addAttribute("name", name);
        model.addAttribute("color", color);
        model.addAttribute("type", type);
        model.addAttribute("rarity", rarity);
        model.addAttribute("matchType", matchType);
        model.addAttribute("isCollection", false);
        return "cards";
    }

    @PostMapping("/add")
    public String addToCollection(
            @RequestParam String cardId,
            @RequestParam String cardName,
            Principal principal
    ) {
        String user = principal.getName();
        collectionService.addCard(user, cardId);
        String encoded = UriUtils.encode(cardName, StandardCharsets.UTF_8);
        return "redirect:/cards?success=true&addedCard=" + encoded;
    }

    @PostMapping("/remove")
    public String removeFromCollection(
            @RequestParam String cardId,
            Principal principal
    ) {
        String user = principal.getName();
        collectionService.removeCard(user, cardId);
        return "redirect:/cards/collection";
    }

    @GetMapping("/collection")
    public String viewCollection(Model model, Principal principal) {
        String user = principal.getName();
        List<Card> cards = collectionService.getUserCollection(user);
        model.addAttribute("cards", cards);
        model.addAttribute("isCollection", true);
        model.addAttribute("showSearchOnly", false);
        model.addAttribute("name", null);
        model.addAttribute("color", null);
        model.addAttribute("type", null);
        model.addAttribute("rarity", null);
        return "cards";
    }

    @GetMapping("/{id}")
    public String viewCardDetail(@PathVariable String id, Model model) {
        String url = "https://api.scryfall.com/cards/" + id;
        try {
            Card card = restTemplate.getForObject(url, Card.class);
            model.addAttribute("cardDetails", card);
        } catch (RestClientException e) {
            model.addAttribute("apiError", "Could not fetch card details.");
        }
        return "card-detail";
    }
}
