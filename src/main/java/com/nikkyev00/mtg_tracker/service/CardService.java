package com.nikkyev00.mtg_tracker.service;

import com.nikkyev00.mtg_tracker.model.CardResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class CardService {

    private final WebClient webClient;

    public CardService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://api.magicthegathering.io/v1/").build();
    }

    public Mono<CardResponse> getCardData() {
        return this.webClient.get()
            .uri("cards")
            .retrieve()
            .bodyToMono(CardResponse.class);
    }
}
