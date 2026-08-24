package com.maslov.booksmaslov.service.impl;

import com.maslov.booksmaslov.domain.Author;
import com.maslov.booksmaslov.domain.Book;
import com.maslov.booksmaslov.domain.Genre;
import com.maslov.booksmaslov.domain.YearOfPublish;
import com.maslov.booksmaslov.exception.NoBookException;
import com.maslov.booksmaslov.model.BookDto;
import com.maslov.booksmaslov.repository.AuthorRepo;
import com.maslov.booksmaslov.repository.BookRepo;
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

    private final BookRepo bookDao;
    private final AuthorRepo authorDao;

    public BookServiceImpl(BookRepo bookDao, AuthorRepo authorDao) {
        this.bookDao = bookDao;
        this.authorDao = authorDao;
    }

    @Override
    @Transactional(readOnly = true)
    public Book getBook(long id) {
        Book book = bookDao.getBookById(id);
        if (nonNull(book)) {
            return book;
        } else {
            log.info("Book with this id {} is not exist", id);
            throw new NoBookException("Book is not exist");
        }
    }

    @Override
    public void getAllBook() {
        List<Book> books = bookDao.getAllBook();
        for (Book book : books) {
            System.out.println(book);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public Book createBook(BookDto bookDto) {
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
        Set<Author> setAuthors = setAuthors(authors);
        for (var a : setAuthors) {
            book.addAuthors(a);
        }
        //todo fix non unique
        book.setYear(year);
        book.setGenre(genre);
        return bookDao.createBook(book);
    }

    @Transactional
    @Override
    public void updateBook(long id, BookDto bookDto) {
        Book bookFromDB = bookDao.getBookById(id);

        mapDtoToEntity(bookDto, bookFromDB);

        bookDao.updateBook(bookFromDB);
    }

    @Override
    public void delBook(long id) {
        bookDao.deleteBook(bookDao.getBookById(id));
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
                authorsOfBook.add(authorDao.getByName(author));
            } catch (RuntimeException e) {
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
