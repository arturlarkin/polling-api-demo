package com.example.polling_api_demo.repositories;

import com.example.polling_api_demo.entities.Poll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PollRepository extends JpaRepository<Poll, Long> {
    List<Poll> findAllByUserId(Long id);
}
