package com.nikkyev00.mtg_tracker.controller;

import com.nikkyev00.mtg_tracker.model.Card;
import com.nikkyev00.mtg_tracker.service.CollectionService;
import com.nikkyev00.mtg_tracker.service.ScryfallSearchService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/cards")
public class CardViewController {

    private final ScryfallSearchService searchService;
    private final CollectionService collectionService;

    public CardViewController(
            ScryfallSearchService searchService,
            CollectionService collectionService
    ) {
        this.searchService = searchService;
        this.collectionService = collectionService;
    }

    @GetMapping
    public String searchCards(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String rarity,
            @RequestParam(required = false) String matchType,
            @RequestParam(required = false) Boolean success,
            @RequestParam(required = false) String addedCard,
            Model model
    ) {
        List<Card> cards = Collections.emptyList();

        boolean hasFilter =
                (name != null && !name.isBlank()) ||
                (color != null && !color.isBlank()) ||
                (type != null && !type.isBlank()) ||
                (rarity != null && !rarity.isBlank());

        if (hasFilter) {
            cards = searchService.search(name, color, type, rarity, matchType);
        }

        model.addAttribute("cards", cards);
        model.addAttribute("resultCount", cards.size()); // ✅ NEW
        model.addAttribute("name", name);
        model.addAttribute("color", color);
        model.addAttribute("type", type);
        model.addAttribute("rarity", rarity);
        model.addAttribute("matchType", matchType);
        model.addAttribute("isCollection", false);
        model.addAttribute("showSearchOnly", !hasFilter);
        model.addAttribute("success", success);
        model.addAttribute("addedCard", addedCard);

        return "cards";
    }

    @PostMapping("/add")
    public String addToCollection(
            @RequestParam String cardId,
            @RequestParam String cardName,
            Principal principal
    ) {
        collectionService.addCard(principal.getName(), cardId);
        return "redirect:/cards?success=true&addedCard=" +
                java.net.URLEncoder.encode(cardName, StandardCharsets.UTF_8);
    }

    @PostMapping("/remove")
    public String removeFromCollection(
            @RequestParam String cardId,
            Principal principal
    ) {
        collectionService.removeCard(principal.getName(), cardId);
        return "redirect:/cards/collection";
    }

    @GetMapping("/collection")
    public String viewCollection(Model model, Principal principal) {
        List<Card> cards = collectionService.getUserCollection(principal.getName());

        model.addAttribute("cards", cards);
        model.addAttribute("resultCount", cards.size()); // consistent
        model.addAttribute("isCollection", true);
        model.addAttribute("showSearchOnly", false);

        return "cards";
    }
}
