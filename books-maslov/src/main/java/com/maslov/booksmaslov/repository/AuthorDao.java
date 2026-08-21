package com.maslov.booksmaslov.repository;

import com.maslov.booksmaslov.domain.Author;

import java.util.List;
import java.util.Optional;

public interface AuthorDao {
    List<Author> getAllAuthors();

    List<Author> getByName(String name);

    Optional<Author> getAuthorById(long id);

    Author createAuthor(Author author);
}
