package com.nikkyev00.mtg_tracker.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class WebClientService {

    private final WebClient.Builder webClientBuilder;

    public WebClientService(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    public Mono<String> getExampleData() {
        return webClientBuilder.baseUrl("https://jsonplaceholder.typicode.com")
                               .build()
                               .get()
                               .uri("/todos/1")
                               .retrieve()
                               .bodyToMono(String.class);
    }
}
