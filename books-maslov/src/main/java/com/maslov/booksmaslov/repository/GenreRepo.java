package com.maslov.booksmaslov.repository;

import com.maslov.booksmaslov.domain.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenreRepo extends JpaRepository<Genre, Long> {
    Genre findByName(String name);
}
