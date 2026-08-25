package com.maslov.booksmaslov.service.impl;

import com.maslov.booksmaslov.domain.Book;
import com.maslov.booksmaslov.exception.NoBookException;
import com.maslov.booksmaslov.mapper.BookMapper;
import com.maslov.booksmaslov.model.BookDto;
import com.maslov.booksmaslov.repository.BookRepo;
import com.maslov.booksmaslov.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.nonNull;

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
    public List<BookDto> getAllBook() {
        List<BookDto> books = new ArrayList<>();
        for (var b: bookRepo.getAllBook()) {
            books.add(mapper.toDto(b));
        }
        return books;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public BookDto createBook(BookDto bookDto) {
        Book book = mapper.toEntity(bookDto);
        var bookFromDb = bookRepo.createBook(book);
        return mapper.toDto(bookFromDb);
    }

    @Transactional
    @Override
    public BookDto updateBook(long id, BookDto bookDto) {
        //todo handle null
        Book bookFromDB = bookRepo.getBookById(id).get();

        Book book = mapper.toEntity(bookDto, bookFromDB);
        Book updatedBook = bookRepo.updateBook(book);

        return mapper.toDto(updatedBook);
    }

    @Transactional
    @Override
    public void delBook(long id) {
        //todo handle null
        Book book = bookRepo.getBookById(id).get();
        bookRepo.deleteBook(book);
        log.info("Book deleted successfully");
    }
}
