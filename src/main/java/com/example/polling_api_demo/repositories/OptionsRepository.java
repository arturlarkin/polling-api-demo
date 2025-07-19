package com.example.polling_api_demo.repositories;

import com.example.polling_api_demo.entities.Options;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OptionsRepository extends JpaRepository<Options, Long> {
}
