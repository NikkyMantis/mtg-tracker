package com.nikkyev00.mtg_tracker.controller;

import com.nikkyev00.mtg_tracker.model.Card;
import com.nikkyev00.mtg_tracker.model.CardDataWrapper;
import com.nikkyev00.mtg_tracker.model.CardWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Controller for viewing and managing MTG cards and the user's collection.
 */
@Controller
@RequestMapping("/cards")
@SessionAttributes("myCollection")
public class CardViewController {

    private final RestTemplate restTemplate = new RestTemplate();

    @ModelAttribute("myCollection")
    public List<Card> initializeCollection() {
        return new ArrayList<>();
    }

    @ModelAttribute("card")
    public Card emptyCard() {
        return new Card();
    }

    /**
     * GET /cards/search/view → search MTG API and display results
     */
    @GetMapping("/search/view")
    public String searchCardsForView(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String rarity,
            @RequestParam(defaultValue = "1") int page,
            Model model
    ) {
        StringBuilder url = new StringBuilder(
                "https://api.magicthegathering.io/v1/cards?page=" + page + "&pageSize=100"
        );
        if (name != null && !name.isEmpty())   url.append("&name=").append(name);
        if (color != null && !color.isEmpty())  url.append("&colors=").append(color);
        if (type != null && !type.isEmpty())   url.append("&types=").append(type);
        if (rarity != null && !rarity.isEmpty()) url.append("&rarity=").append(rarity);

        List<Card> cards;
        try {
            CardDataWrapper response = restTemplate.getForObject(url.toString(), CardDataWrapper.class);
            cards = (response != null) ? response.getCards() : List.of();
        } catch (HttpServerErrorException e) {
            System.err.println("MTG API server error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            model.addAttribute("apiError", "The MTG API is currently unavailable. Please try again later.");
            cards = List.of();
        } catch (RestClientException e) {
            System.err.println("Error fetching cards: " + e.getMessage());
            model.addAttribute("apiError", "An error occurred while fetching cards. Please try again.");
            cards = List.of();
        }

        // DEBUG LOGGING: print each card’s name and multiverseId
        cards.forEach(c ->
            System.out.println("DEBUG → " 
                + c.getName() 
                + " → multiverseId=" 
                + c.getMultiverseId()));

        model.addAttribute("cards", cards);
        model.addAttribute("name", name);
        model.addAttribute("color", color);
        model.addAttribute("type", type);
        model.addAttribute("rarity", rarity);
        model.addAttribute("page", page);
        model.addAttribute("isCollection", false);
        return "cards";
    }

    /**
     * POST /cards/add → add a card to the session collection
     */
    @PostMapping("/add")
    public String addToCollection(
            @ModelAttribute("myCollection") List<Card> myCollection,
            @ModelAttribute("card") Card card,
            @RequestParam(value = "searchName", required = false) String name,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String rarity
    ) {
        if (card.getName() != null && !card.getName().isBlank()) {
            myCollection.add(card);
        }
        StringBuilder redirectUrl = new StringBuilder("redirect:/cards/search/view?");
        if (name != null && !name.isEmpty())   redirectUrl.append("name=").append(name).append("&");
        if (color != null && !color.isEmpty())  redirectUrl.append("color=").append(color).append("&");
        if (type != null && !type.isEmpty())   redirectUrl.append("type=").append(type).append("&");
        if (rarity != null && !rarity.isEmpty()) redirectUrl.append("rarity=").append(rarity).append("&");
        redirectUrl.append("success=true&addedCard=").append(card.getName().replaceAll(" ", "+"));
        return redirectUrl.toString();
    }

    /**
     * POST /cards/remove → remove a card from the session collection by multiverseId
     */
    @PostMapping("/remove")
    public String removeFromCollection(
            @ModelAttribute("myCollection") List<Card> myCollection,
            @ModelAttribute("card") Card card
    ) {
        myCollection.removeIf(c -> Objects.equals(c.getMultiverseId(), card.getMultiverseId()));
        return "redirect:/cards/collection";
    }

    /**
     * GET /cards/collection → view the session collection
     */
    @GetMapping("/collection")
    public String viewCollection(
            @ModelAttribute("myCollection") List<Card> myCollection,
            Model model
    ) {
        model.addAttribute("cards", myCollection);
        model.addAttribute("isCollection", true);
        return "cards";
    }

    /**
     * GET /cards/{multiverseId} → view details for a single card
     */
    @GetMapping("/{multiverseId}")
    public String viewCardDetail(
            @PathVariable Integer multiverseId,
            Model model
    ) {
        String url = "https://api.magicthegathering.io/v1/cards/" + multiverseId;
        CardWrapper response;
        try {
            response = restTemplate.getForObject(url, CardWrapper.class);
        } catch (RestClientException e) {
            model.addAttribute("apiError", "Could not fetch card details.");
            return "card-detail";
        }
        Card card = (response != null) ? response.getCard() : null;
        model.addAttribute("cardDetails", card);
        return "card-detail";
    }
}
