package com.maslov.booksmaslov.repository;

import com.maslov.booksmaslov.domain.Book;

import java.util.List;

public interface BookDao {
    List<Book> getAllBook();

    Book getBookById(long id);

    List<Book> getBooksByName(String name);

    Book createBook(Book book);

    void deleteBook(Book book);

    Book updateBook(Book book);
}
