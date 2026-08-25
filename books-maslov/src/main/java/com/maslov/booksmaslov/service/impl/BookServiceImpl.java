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
        String year = bookDto.getYear();
        String genre = bookDto.getGenre();

        Set<Author> setAuthors = getAuthorsSet(authors);
        for (var a : setAuthors) {
            book.addAuthors(a);
        }

        book.setTitle(title);
        book.setYear(getYear(year));
        book.setGenre(getGenre(genre));
        var bookFromDb = bookRepo.createBook(book);

        return mapper.toDto(bookFromDb);
    }

    @Transactional
    @Override
    public BookDto updateBook(long id, BookDto bookDto) {
        Book bookFromDB = bookRepo.getBookById(id);

        mapDtoToEntity(bookDto, bookFromDB);
        Book updatedBook = bookRepo.updateBook(bookFromDB);

        return mapper.toDto(updatedBook);
    }

    @Transactional
    @Override
    public void delBook(long id) {
        Book book = bookRepo.getBookById(id);
        bookRepo.deleteBook(book);
        log.info("Book deleted successfully");
    }

    private Genre getGenre(String genreStr) {
        return genreRepo.getGenreByName(genreStr).orElseGet(() -> {
            Genre newGenre = new Genre(genreStr);
            return genreRepo.createGenre(newGenre);
        });
    }

    private YearOfPublish getYear(String yearStr) {
        return yearRepo.getYearByDate(yearStr).orElseGet(() -> {
            YearOfPublish newYearOfPublish = new YearOfPublish(yearStr);
            return yearRepo.createYear(newYearOfPublish);
        });
    }

    private Set<Author> getAuthorsSet(Set<String> authors) {
        Set<Author> authorsOfBook = new HashSet<>();
        for (var author : authors) {

            authorsOfBook.add(authorRepo.getByName(author).orElseGet(() -> {
                Author newAuthor = new Author(author);
                return authorRepo.createAuthor(newAuthor);
            }));
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
            entity.setYear(getYear(book.getYear()));
        }
        if (nonNull(book.getGenre())) {
            entity.setGenre(getGenre(book.getGenre()));
        }
        Set<Author> authors = getAuthorsSet(authorsFormUser);
        entity.setAuthors(authors);
    }
}
