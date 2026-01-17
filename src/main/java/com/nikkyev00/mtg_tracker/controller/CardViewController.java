package com.nikkyev00.mtg_tracker.controller;

import com.nikkyev00.mtg_tracker.model.Card;
import com.nikkyev00.mtg_tracker.model.CollectionItem;
import com.nikkyev00.mtg_tracker.service.CollectionService;
import com.nikkyev00.mtg_tracker.service.ScryfallSearchService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class CardViewController {

    private final ScryfallSearchService scryfallService;
    private final CollectionService collectionService;

    public CardViewController(
            ScryfallSearchService scryfallService,
            CollectionService collectionService
    ) {
        this.scryfallService = scryfallService;
        this.collectionService = collectionService;
    }

    /* =========================
       SEARCH
       ========================= */
    @GetMapping("/cards")
    public String searchCards(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String rarity,
            Model model
    ) {
        boolean hasSearch = name != null && !name.isBlank();

        List<Card> cards = hasSearch
                ? scryfallService.search(name, color, type, rarity, "exact")
                : List.of();

        model.addAttribute("cards", cards);
        model.addAttribute("isCollection", false);
        model.addAttribute("showSearchOnly", !hasSearch);

        model.addAttribute("name", name);
        model.addAttribute("color", color);
        model.addAttribute("type", type);
        model.addAttribute("rarity", rarity);

        return "cards";
    }

    /* =========================
       ADD
       ========================= */
    @PostMapping("/cards/add")
    public String addCard(
            @RequestParam String cardId,
            @RequestParam String cardName,
            Principal principal,
            RedirectAttributes redirectAttributes
    ) {
        collectionService.addToCollection(principal.getName(), cardId);

        redirectAttributes.addFlashAttribute("success", true);
        redirectAttributes.addFlashAttribute("addedCard", cardName);

        return "redirect:/cards";
    }

    /* =========================
       REMOVE
       ========================= */
    @PostMapping("/cards/remove")
    public String removeCard(
            @RequestParam String cardId,
            Principal principal
    ) {
        collectionService.removeFromCollection(principal.getName(), cardId);
        return "redirect:/cards/collection";
    }

    /* =========================
       COLLECTION (FIXED)
       ========================= */
    @GetMapping("/cards/collection")
    public String viewCollection(
            Principal principal,
            Model model
    ) {
        List<CollectionItem> items =
                collectionService.getUserCollection(principal.getName());

        List<Card> cards = new ArrayList<>();

        for (CollectionItem item : items) {
            Card card = scryfallService.getCardById(item.getCardId());
            if (card != null) {
                cards.add(card);
            }
        }

        model.addAttribute("cards", cards);
        model.addAttribute("isCollection", true);
        model.addAttribute("showSearchOnly", false);

        return "cards";
    }
}
