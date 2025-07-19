package com.example.polling_api_demo.repositories;

import com.example.polling_api_demo.entities.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    boolean existsByPollIdAndUserId(Long pollId, Long userId);
    boolean existsByPollIdAndUserIdAndOptionsId(Long pollId, Long userId, Long optionId);
    Optional<Vote> findByPollIdAndUserId(Long pollId, Long userId);
}
