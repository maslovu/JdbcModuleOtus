package com.maslov.booksmaslov.service.impl;

import com.maslov.booksmaslov.domain.Author;
import com.maslov.booksmaslov.domain.Book;
import com.maslov.booksmaslov.domain.Genre;
import com.maslov.booksmaslov.domain.YearOfPublish;
import com.maslov.booksmaslov.exception.NoBookException;
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

    public BookServiceImpl(BookRepo bookRepo, AuthorRepo authorRepo, GenreRepo genreRepo, YearRepo yearRepo) {
        this.bookRepo = bookRepo;
        this.authorRepo = authorRepo;
        this.genreRepo = genreRepo;
        this.yearRepo = yearRepo;
    }

    @Override
    @Transactional(readOnly = true)
    public Book getBook(long id) {
        Book book = bookRepo.getBookById(id);
        if (nonNull(book)) {
            return book;
        } else {
            log.info("Book with this id {} is not exist", id);
            throw new NoBookException("Book is not exist");
        }
    }

    @Override
    public List<Book> getAllBook() {
        return bookRepo.getAllBook();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public BookDto createBook(BookDto bookDto) {
        Book book = new Book();
        String title = bookDto.title();
        String authorFromUser = bookDto.authors();
        Set<String> authors = Stream.of(authorFromUser.split(","))
                .map(String::new)
                .collect(Collectors.toSet());
        String yearStr = bookDto.year();
        var year = new YearOfPublish(yearStr);
        String genreStr = bookDto.genre();
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

        return new BookDto(bookFromDb.getTitle(), bookFromDb.getAuthors().toString(),
                bookFromDb.getYear().toString(), bookFromDb.getGenre().toString());
    }

    @Transactional
    @Override
    public Book updateBook(long id, BookDto bookDto) {
        Book bookFromDB = bookRepo.getBookById(id);

        mapDtoToEntity(bookDto, bookFromDB);

        return bookRepo.updateBook(bookFromDB);
    }

    @Override
    public void delBook(long id) {
        bookRepo.deleteBook(bookRepo.getBookById(id));
        log.info("Book deleted successfully");
    }

//    @Override
//    @Transactional(readOnly = true)
//    public Set<Comment> getComments() {
//        System.out.println(ENTER_ID);
//        int id = helper.getIdFromUser();
//        var comments = bookDao.getBookById(id).getComments();
//        System.out.println(comments);
//        return comments;
//    }

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
        Set<String> authorsFormUser = Stream.of(book.authors().split(","))
                .map(String::new)
                .collect(Collectors.toSet());
        if (nonNull(book.title())) {
            entity.setTitle(book.title());
        }
        if (nonNull(book.year())) {
            entity.setYear(new YearOfPublish(book.year()));
        }
        if (nonNull(book.genre())) {
            entity.setGenre(new Genre(book.genre()));
        }
        Set<Author> authors = setAuthors(authorsFormUser);
        entity.setAuthors(authors);
    }
}
