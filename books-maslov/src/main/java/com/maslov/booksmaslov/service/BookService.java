package com.maslov.booksmaslov.service;

import com.maslov.booksmaslov.domain.Book;
import com.maslov.booksmaslov.model.BookDto;

import java.util.List;

public interface BookService {
    Book getBook(long id);

    List<Book> getAllBook();

    BookDto createBook(BookDto bookDto);

    Book updateBook(long bookId, BookDto bookDto);

    void delBook(long id);

}
