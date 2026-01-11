package com.nikkyev00.mtg_tracker.service;

import com.nikkyev00.mtg_tracker.model.Card;
import com.nikkyev00.mtg_tracker.model.CollectionItem;
import com.nikkyev00.mtg_tracker.repository.CollectionItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CollectionService {

    private final CollectionItemRepository repo;
    private final RestTemplate restTemplate = new RestTemplate();

    public CollectionService(CollectionItemRepository repo) {
        this.repo = repo;
    }

    public void addCard(String username, String cardId) {
        CollectionItem item = new CollectionItem(username, cardId);
        repo.save(item);
    }

    public void removeCard(String username, String cardId) {
        repo.deleteByUsernameAndCardId(username, cardId);
    }

    public List<Card> getUserCollection(String username) {
        return repo.findByUsername(username).stream()
            .map(i -> restTemplate.getForObject(
                    "https://api.scryfall.com/cards/" + i.getCardId(),
                    Card.class))
            .collect(Collectors.toList());
    }
}
