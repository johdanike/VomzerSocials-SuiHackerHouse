package org.vomzersocials.data.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.vomzersocials.data.models.Token;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, String> {
    Optional<Token> findByOwnerEmail(String lowerCase);
}