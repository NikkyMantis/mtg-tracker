package com.nikkyev00.mtg_tracker.repository;

import com.nikkyev00.mtg_tracker.model.CollectionItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CollectionItemRepository extends JpaRepository<CollectionItem, Long> {

    List<CollectionItem> findByUsername(String username);

    void deleteByUsernameAndCardId(String username, String cardId);
}
