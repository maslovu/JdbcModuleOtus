package com.maslov.booksmaslov.repository;

import com.maslov.booksmaslov.domain.Author;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuthorRepo extends JpaRepository<Author, Long> {
    List<Author> findByName(String text);
}
