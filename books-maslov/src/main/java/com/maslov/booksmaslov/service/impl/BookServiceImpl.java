package com.maslov.booksmaslov.service.impl;

import com.maslov.booksmaslov.domain.Book;
import com.maslov.booksmaslov.exception.NoBookException;
import com.maslov.booksmaslov.mapper.BookMapper;
import com.maslov.booksmaslov.dto.BookDto;
import com.maslov.booksmaslov.repository.BookRepo;
import com.maslov.booksmaslov.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
public class BookServiceImpl implements BookService {

    private final BookRepo bookRepo;
    private final BookMapper mapper;

    public BookServiceImpl(BookRepo bookRepo, BookMapper mapper) {
        this.bookRepo = bookRepo;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public BookDto getBook(long id) {
        var book = bookRepo.findById(id)
                .orElseThrow(() -> new NoBookException("Book with id " + id + " does not exist"));
        return mapper.toDto(book);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookDto> getAllBook() {
        return bookRepo.findAllBooks();
    }

    @Transactional
    @Override
    public BookDto createBook(BookDto bookDto) {
        Book bookEntity = mapper.toEntity(bookDto);

        Book savedEntity = bookRepo.save(bookEntity);

        Book freshCopy = bookRepo.findById(savedEntity.getId())
                .orElseThrow(() -> new IllegalStateException("Книга исчезла сразу после сохранения"));

        return mapper.toDto(freshCopy);
    }

    @Transactional
    @Override
    public BookDto updateBook(long id, BookDto bookDto) {
        Book existingBook = bookRepo.findById(id)
                .orElseThrow(() -> new NoBookException("Book with id " + id + " does not exist"));

        mapper.updateEntityFromDto(bookDto, existingBook);

        return mapper.toDto(existingBook);
    }

    @Transactional
    @Override
    public void delBook(long id) {
        //Отсутствие книги не является ошибкой (метод должен быть идемпотентным)
        bookRepo.deleteById(id);
    }
}
