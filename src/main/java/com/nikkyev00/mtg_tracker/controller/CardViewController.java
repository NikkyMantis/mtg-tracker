package com.nikkyev00.mtg_tracker.controller;

import com.nikkyev00.mtg_tracker.model.Card;
import com.nikkyev00.mtg_tracker.model.OracleCard;
import com.nikkyev00.mtg_tracker.service.CardService;
import com.nikkyev00.mtg_tracker.service.CollectionService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collection;
import java.util.List;

@Controller
@RequestMapping("/cards")
public class CardViewController {

    private final CardService cardService;
    private final CollectionService collectionService;

    public CardViewController(CardService cardService,
                              CollectionService collectionService) {
        this.cardService = cardService;
        this.collectionService = collectionService;
    }

    /* ================= SEARCH ================= */

    @GetMapping
    public String searchCards(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String rarity,
            Model model
    ) {
        if (name == null && color == null && type == null && rarity == null) {
            model.addAttribute("showSearchOnly", true);
            model.addAttribute("isCollection", false);
            return "cards";
        }

        List<Card> cards = cardService.searchCards(name, color, type, rarity);

        model.addAttribute("cards", cards);
        model.addAttribute("isCollection", false);
        model.addAttribute("name", name);
        model.addAttribute("color", color);
        model.addAttribute("type", type);
        model.addAttribute("rarity", rarity);

        return "cards";
    }

    /* ================= VIEW PRINTINGS ================= */

    @GetMapping("/printings/{oracleId}")
    public String viewPrintings(@PathVariable String oracleId, Model model) {

        List<Card> printings = cardService.getPrintingsByCardId(oracleId);

        model.addAttribute("cards", printings);
        model.addAttribute("isCollection", false);

        return "printings";
    }

    /* ================= ADD ================= */

    @PostMapping("/add")
    public String addCard(
            @RequestParam String cardId,
            @RequestParam String cardName,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        String username = authentication.getName();

        Card card = cardService.getCardById(cardId);
        if (card != null) {
            collectionService.addToCollection(username, card);
        }

        redirectAttributes.addFlashAttribute("success", true);
        redirectAttributes.addFlashAttribute("addedCard", cardName);

        return "redirect:/cards";
    }

    /* ================= COLLECTION ================= */

    @GetMapping("/collection")
    public String viewCollection(Authentication authentication, Model model) {

        String username = authentication.getName();

        Collection<OracleCard> oracleCards =
                collectionService.getUserCollection(username);

        model.addAttribute("oracleCards", oracleCards);
        model.addAttribute("isCollection", true);

        return "collection";
    }

    /* ================= REMOVE PRINTING ================= */

    @PostMapping("/remove")
    public String removePrinting(
            @RequestParam String cardId,
            Authentication authentication
    ) {
        String username = authentication.getName();
        collectionService.removePrinting(username, cardId);
        return "redirect:/cards/collection";
    }
}
