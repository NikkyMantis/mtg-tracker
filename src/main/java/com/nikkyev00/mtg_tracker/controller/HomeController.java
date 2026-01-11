package com.nikkyev00.mtg_tracker.controller;

import com.nikkyev00.mtg_tracker.model.Card;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
public class HomeController {

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * GET /
     * Fetches 10 unique, non-land random cards from Scryfall and renders the home page.
     * Displays Sign In if anonymous, or username dropdown when signed in.
     */
    @GetMapping("/")
    public String home(Model model, Principal principal) {
        List<Card> randomCards = new ArrayList<>();
        Set<String> seenIds = new HashSet<>();
        String url = "https://api.scryfall.com/cards/random";
        int attempts = 0;

        try {
            // Keep fetching until we have 5 unique non-land cards or reach attempt limit
            while (randomCards.size() < 5 && attempts < 50) {
                attempts++;
                Card card = restTemplate.getForObject(url, Card.class);
                if (card == null) continue;

                String id = card.getId();
                String type = (card.getType() != null) ? card.getType().toLowerCase() : "";

                // Skip duplicates and any land cards
                if (seenIds.contains(id) || type.contains("land")) {
                    continue;
                }

                randomCards.add(card);
                seenIds.add(id);
            }
        } catch (RestClientException e) {
            // Log error; page renders with whatever cards were fetched
            System.err.println("Error fetching random cards: " + e.getMessage());
        }

        model.addAttribute("topCards", randomCards);

        // Expose username if signed in
        if (principal != null) {
            model.addAttribute("username", principal.getName());
        }

        return "home";
    }
}
