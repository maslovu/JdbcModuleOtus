package com.maslov.booksmaslov.service;

import com.maslov.booksmaslov.dto.BookDto;

import java.util.List;

public interface BookService {
    BookDto getBook(long id);

    List<BookDto> getAllBook();

    BookDto createBook(BookDto bookDto);

    BookDto updateBook(long bookId, BookDto bookDto);

    void delBook(long id);

}
