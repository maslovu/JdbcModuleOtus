package com.maslov.booksmaslov.repository;

import com.maslov.booksmaslov.domain.Author;

import java.util.Set;

public interface AuthorDao {
    Set<Author> getAllAuthors();

    Author getByName(String name);

    Author getAuthorById(long id);

    Author createAuthor(Author author);
}
