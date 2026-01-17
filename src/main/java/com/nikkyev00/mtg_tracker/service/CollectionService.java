package com.nikkyev00.mtg_tracker.service;

import com.nikkyev00.mtg_tracker.model.CollectionItem;
import com.nikkyev00.mtg_tracker.repository.CollectionItemRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CollectionService {

    private final CollectionItemRepository collectionItemRepository;

    public CollectionService(CollectionItemRepository collectionItemRepository) {
        this.collectionItemRepository = collectionItemRepository;
    }

    /* =========================
       ADD TO COLLECTION
       ========================= */
    public void addToCollection(String username, String cardId) {
        CollectionItem item = new CollectionItem(username, cardId);
        collectionItemRepository.save(item);
    }

    /* =========================
       REMOVE FROM COLLECTION
       ========================= */
    @Transactional
    public void removeFromCollection(String username, String cardId) {
        collectionItemRepository.deleteByUsernameAndCardId(username, cardId);
    }

    /* =========================
       GET USER COLLECTION
       ========================= */
    public List<CollectionItem> getUserCollection(String username) {
        return collectionItemRepository.findByUsername(username);
    }
}
