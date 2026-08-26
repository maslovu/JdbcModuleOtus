package com.maslov.booksmaslov.repository;

import com.maslov.booksmaslov.domain.Author;

import java.util.Optional;
import java.util.Set;

public interface AuthorRepo {
    Set<Author> getAllAuthors();

    Optional<Author> getByName(String name);

    Author getAuthorById(long id);

    Author createAuthor(Author author);
}
