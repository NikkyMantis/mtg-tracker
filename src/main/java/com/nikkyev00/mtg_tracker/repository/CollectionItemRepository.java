package com.nikkyev00.mtg_tracker.repository;

import com.nikkyev00.mtg_tracker.model.CollectionItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CollectionItemRepository extends JpaRepository<CollectionItem, Long> {

    List<CollectionItem> findByUsername(String username);

    @Modifying
    @Transactional
    @Query("DELETE FROM CollectionItem c WHERE c.username = :username AND c.cardId = :cardId")
    void deleteByUsernameAndCardId(String username, String cardId);
}
