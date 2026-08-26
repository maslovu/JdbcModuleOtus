package com.maslov.booksmaslov.service.impl;

import com.maslov.booksmaslov.domain.Book;
import com.maslov.booksmaslov.exception.NoBookException;
import com.maslov.booksmaslov.mapper.BookMapper;
import com.maslov.booksmaslov.model.BookDto;
import com.maslov.booksmaslov.repository.BookRepo;
import com.maslov.booksmaslov.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BookServiceImpl implements BookService {

    private final BookRepo bookRepo;
    private final BookMapper mapper;

    public BookServiceImpl(BookRepo bookRepo,
                           BookMapper mapper) {
        this.bookRepo = bookRepo;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public BookDto getBook(long id) {
        Optional<Book> book = bookRepo.getBookById(id);
        if (book.isPresent()) {
            return mapper.toDto(book.get());
        } else {
            log.info("Book with this id {} is not exist", id);
            throw new NoBookException("Book is not exist");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookDto> getAllBook() {
        return bookRepo.getAllBooks().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public BookDto createBook(BookDto bookDto) {
        Book book = mapper.toEntity(bookDto);
        var bookFromDb = bookRepo.createBook(book);
        return mapper.toDto(bookFromDb);
    }

    @Transactional
    @Override
    public BookDto updateBook(long id, BookDto bookDto) {
        Book bookFromDB = bookRepo.getBookById(id)
                .orElseThrow(() -> new NoBookException("Book with id " + id + " does not exist"));

        Book book = mapper.toEntity(bookDto, bookFromDB);
        Book updatedBook = bookRepo.updateBook(book);

        return mapper.toDto(updatedBook);
    }

    @Transactional
    @Override
    public void delBook(long id) {
        Book book = bookRepo.getBookById(id)
                .orElseThrow(() -> new NoBookException("Book with id " + id + " does not exist"));

        bookRepo.deleteBook(book);
        log.info("Book deleted successfully");
    }
}
