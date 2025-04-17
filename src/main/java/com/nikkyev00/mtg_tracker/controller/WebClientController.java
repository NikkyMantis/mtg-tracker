package com.nikkyev00.mtg_tracker.controller;

import com.nikkyev00.mtg_tracker.service.WebClientService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class WebClientController {

    private final WebClientService webClientService;

    public WebClientController(WebClientService webClientService) {
        this.webClientService = webClientService;
    }

    @GetMapping("/test-webclient")
    public Mono<String> testWebClient() {
        return webClientService.getExampleData();
    }
}
