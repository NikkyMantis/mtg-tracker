package com.nikkyev00.mtg_tracker.controller;

import com.nikkyev00.mtg_tracker.model.Card;
import com.nikkyev00.mtg_tracker.model.CardDataWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/cards")
@SessionAttributes("myCollection")
public class CardViewController {

    private final RestTemplate restTemplate = new RestTemplate();

    @ModelAttribute("myCollection")
    public List<Card> initializeCollection() {
        return new ArrayList<>();
    }

    @GetMapping("/search/view")
    public String searchCardsForView(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String rarity,
            Model model
    ) {
        StringBuilder url = new StringBuilder("https://api.magicthegathering.io/v1/cards?");

        if (name != null && !name.isEmpty()) url.append("name=").append(name).append("&");
        if (color != null && !color.isEmpty()) url.append("colors=").append(color).append("&");
        if (type != null && !type.isEmpty()) url.append("types=").append(type).append("&");
        if (rarity != null && !rarity.isEmpty()) url.append("rarity=").append(rarity).append("&");

        CardDataWrapper response = restTemplate.getForObject(url.toString(), CardDataWrapper.class);
        List<Card> cards = response != null ? response.getCards() : List.of();

        model.addAttribute("cards", cards);
        model.addAttribute("name", name);
        model.addAttribute("color", color);
        model.addAttribute("type", type);
        model.addAttribute("rarity", rarity);
        model.addAttribute("isCollection", false);

        return "cards";
    }

    @PostMapping("/add")
    public String addToCollection(
            @ModelAttribute("myCollection") List<Card> myCollection,
            @ModelAttribute Card card,
            @RequestParam(value = "searchName", required = false) String name,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String rarity
    ) {
        System.out.println(">>> SUBMITTED CARD:");
        System.out.println("Name: " + card.getName());
        System.out.println("Type: " + card.getType());
        System.out.println("Mana Cost: " + card.getManaCost());
        System.out.println("Rarity: " + card.getRarity());
        System.out.println("Image URL: " + card.getImageUrl());
    
        if (card.getName() != null && !card.getName().isBlank()) {
            myCollection.add(card);
            System.out.println("✅ Card added to collection.");
        } else {
            System.out.println("❌ Card name missing. Not added.");
        }
    
        StringBuilder redirectUrl = new StringBuilder("redirect:/cards/search/view?");
        if (name != null && !name.isEmpty()) redirectUrl.append("name=").append(name).append("&");
        if (color != null && !color.isEmpty()) redirectUrl.append("color=").append(color).append("&");
        if (type != null && !type.isEmpty()) redirectUrl.append("type=").append(type).append("&");
        if (rarity != null && !rarity.isEmpty()) redirectUrl.append("rarity=").append(rarity).append("&");
    
        redirectUrl.append("success=true");
        return redirectUrl.toString();
    }
    
    @PostMapping("/remove")
    public String removeFromCollection(
            @ModelAttribute("myCollection") List<Card> myCollection,
            @ModelAttribute Card card
    ) {
        String name = card.getName();
        System.out.println(">>> REMOVING CARD: " + name);
        System.out.println("Before removal, size: " + myCollection.size());
        boolean removed = myCollection.removeIf(c -> name != null && name.equals(c.getName()));
        System.out.println("Card removed: " + removed);
        System.out.println("After removal, size: " + myCollection.size());
    
        return "redirect:/cards/collection";
    }    
    
    @GetMapping("/collection")
    public String viewCollection(
            @ModelAttribute("myCollection") List<Card> myCollection,
            Model model
    ) {
        System.out.println(">>> VIEWING COLLECTION:");
        System.out.println("Collection size: " + myCollection.size());
        for (Card c : myCollection) {
            System.out.println(" - " + c.getName());
        }
    
        model.addAttribute("cards", myCollection);
        model.addAttribute("isCollection", true);
        return "cards";
    }
}