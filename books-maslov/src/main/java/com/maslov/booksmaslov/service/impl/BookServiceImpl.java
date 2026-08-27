package com.maslov.booksmaslov.service.impl;

import com.maslov.booksmaslov.domain.Book;
import com.maslov.booksmaslov.exception.NoBookException;
import com.maslov.booksmaslov.mapper.BookMapper;
import com.maslov.booksmaslov.dto.BookDto;
import com.maslov.booksmaslov.repository.BookRepo;
import com.maslov.booksmaslov.service.BookService;
import lombok.extern.slf4j.Slf4j;
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

        Book savesBook = bookRepo.save(bookEntity);

        return mapper.toDto(savesBook);
    }

    @Transactional
    @Override
    public BookDto updateBook(long id, BookDto bookDto) {
//        return bookRepo.findById(id)
//                .orElseThrow(() -> new NoBookException("Book with id " + id + " does not exist"));
        return null;
//        Book book = mapper.toEntity(bookDto);
//        Book updatedBook = bookRepo.updateBook(book);
//
//        return mapper.toDto(updatedBook);
    }

    @Transactional
    @Override
    public void delBook(long id) {
//        Book book = bookRepo.getBookById(id)
//                .orElseThrow(() -> new NoBookException("Book with id " + id + " does not exist"));
//
//        bookRepo.deleteBook(book);
//        log.info("Book deleted successfully");
    }
}
