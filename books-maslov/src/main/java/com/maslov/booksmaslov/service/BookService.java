package com.maslov.booksmaslov.service;

import com.maslov.booksmaslov.domain.Book;
import com.maslov.booksmaslov.domain.Comment;
import com.maslov.booksmaslov.model.BookDto;

import java.util.Set;

public interface BookService {
    Book getBook(long id);

    void getAllBook();

    Book createBook(BookDto bookDto);

    void updateBook(long bookId, BookDto bookDto);

    void delBook(long id);

}
