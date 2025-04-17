package com.nikkyev00.mtg_tracker.controller;

import com.nikkyev00.mtg_tracker.model.Card;
import com.nikkyev00.mtg_tracker.model.CardDataWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/cards")
public class CardController {

    @Autowired
    private WebClient.Builder webClientBuilder;

    @GetMapping("/search")
    public Mono<List<Card>> searchCardsByName(@RequestParam String name) {
        String url = "https://api.magicthegathering.io/v1/cards?name=" + name;

        return webClientBuilder.build()
            .get()
            .uri(url)
            .retrieve()
            .bodyToMono(CardDataWrapper.class)
            .map(CardDataWrapper::getCards);
    }

    @GetMapping("")
    public Mono<List<Card>> getDefaultCards() {
        String url = "https://api.magicthegathering.io/v1/cards?pageSize=10";

        return webClientBuilder.build()
            .get()
            .uri(url)
            .retrieve()
            .bodyToMono(CardDataWrapper.class)
            .map(CardDataWrapper::getCards);
    }
}
