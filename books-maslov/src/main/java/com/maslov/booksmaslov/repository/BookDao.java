package com.maslov.booksmaslov.repository;

import com.maslov.booksmaslov.domain.Book;

import java.util.List;
import java.util.Optional;

public interface BookDao {
    List<Book> getAllBook();

    Optional<Book> getBookById(long id);

    List<Book> getBooksByName(String name);

    Book createBook(Book book);

    void deleteBook(Book book);

    Book updateBook(Book book);
}
