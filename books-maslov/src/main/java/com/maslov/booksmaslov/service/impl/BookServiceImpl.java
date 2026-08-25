package com.maslov.booksmaslov.service.impl;

import com.maslov.booksmaslov.domain.Author;
import com.maslov.booksmaslov.domain.Book;
import com.maslov.booksmaslov.domain.Genre;
import com.maslov.booksmaslov.domain.YearOfPublish;
import com.maslov.booksmaslov.exception.NoBookException;
import com.maslov.booksmaslov.mapper.BookMapper;
import com.maslov.booksmaslov.model.BookDto;
import com.maslov.booksmaslov.repository.AuthorRepo;
import com.maslov.booksmaslov.repository.BookRepo;
import com.maslov.booksmaslov.repository.GenreRepo;
import com.maslov.booksmaslov.repository.YearRepo;
import com.maslov.booksmaslov.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;

@Service
@Slf4j
public class BookServiceImpl implements BookService {

    private final BookRepo bookRepo;
    private final AuthorRepo authorRepo;
    private final GenreRepo genreRepo;
    private final YearRepo yearRepo;
    private final BookMapper mapper;

    public BookServiceImpl(BookRepo bookRepo,
                           AuthorRepo authorRepo,
                           GenreRepo genreRepo,
                           YearRepo yearRepo,
                           BookMapper mapper) {
        this.bookRepo = bookRepo;
        this.authorRepo = authorRepo;
        this.genreRepo = genreRepo;
        this.yearRepo = yearRepo;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public BookDto getBook(long id) {
        Book book = bookRepo.getBookById(id);
        if (nonNull(book)) {
            return mapper.toDto(book);
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
        Book book = new Book();
        String title = bookDto.getTitle();
        String authorFromUser = bookDto.getAuthors();
        Set<String> authors = Stream.of(authorFromUser.split(","))
                .map(String::new)
                .collect(Collectors.toSet());
        String yearStr = bookDto.getYear();
        String genreStr = bookDto.getGenre();
        var genre = new Genre(genreStr);
        book.setTitle(title);
        //todo fix non unique
        Set<Author> setAuthors = setAuthors(authors);
        for (var a : setAuthors) {
            book.addAuthors(a);
        }
        //todo fix non unique
        //todo rename some fields in Entities
        YearOfPublish yearOfPublish = yearRepo.getYearByDate(yearStr).orElseGet(() -> {
            YearOfPublish newYearOfPublish = new YearOfPublish(yearStr);
            return yearRepo.createYear(newYearOfPublish);
        });

        book.setYear(yearOfPublish);
        //todo fix non unique
        genreRepo.createGenre(genre);
        book.setGenre(genre);
        var bookFromDb = bookRepo.createBook(book);

        return mapper.toDto(bookFromDb);
    }

    @Transactional
    @Override
    public BookDto updateBook(long id, BookDto bookDto) {
        Book bookFromDB = bookRepo.getBookById(id);

        mapDtoToEntity(bookDto, bookFromDB);

        return mapper.toDto(bookRepo.updateBook(bookFromDB));
    }

    @Override
    public void delBook(long id) {
        bookRepo.deleteBook(bookRepo.getBookById(id));
        log.info("Book deleted successfully");
    }

    private Set<Author> setAuthors(Set<String> authors) {
        Set<Author> authorsOfBook = new HashSet<>();
        for (var author : authors) {
            try {
                authorsOfBook.add(authorRepo.getByName(author));
            } catch (RuntimeException e) {
                authorRepo.createAuthor(new Author(author));
                authorsOfBook.add(new Author(author));
            }
        }
        return authorsOfBook;
    }

    private void mapDtoToEntity(BookDto book, Book entity) {
        Set<String> authorsFormUser = Stream.of(book.getAuthors().split(","))
                .map(String::new)
                .collect(Collectors.toSet());
        if (nonNull(book.getTitle())) {
            entity.setTitle(book.getTitle());
        }
        if (nonNull(book.getYear())) {
            entity.setYear(new YearOfPublish(book.getYear()));
        }
        if (nonNull(book.getGenre())) {
            entity.setGenre(new Genre(book.getGenre()));
        }
        Set<Author> authors = setAuthors(authorsFormUser);
        entity.setAuthors(authors);
    }
}
