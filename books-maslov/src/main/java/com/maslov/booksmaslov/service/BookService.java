package com.maslov.booksmaslov.service;

import com.maslov.booksmaslov.dto.BookDto;

import java.util.List;

public interface BookService {
    BookDto getBook(long id);

    List<BookDto> getAllBooks();

    BookDto createBook(BookDto bookDto);

    BookDto updateBook(long bookId, BookDto bookDto);

    void deleteBook(long id);

}
