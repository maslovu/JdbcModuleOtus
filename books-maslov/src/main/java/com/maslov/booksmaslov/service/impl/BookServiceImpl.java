package com.maslov.booksmaslov.service.impl;

import com.maslov.booksmaslov.domain.Author;
import com.maslov.booksmaslov.domain.Book;
import com.maslov.booksmaslov.domain.Comment;
import com.maslov.booksmaslov.domain.Genre;
import com.maslov.booksmaslov.domain.YearOfPublish;
import com.maslov.booksmaslov.repository.AuthorDao;
import com.maslov.booksmaslov.repository.BookDao;
import com.maslov.booksmaslov.service.BookService;
import com.maslov.booksmaslov.service.ScannerHelper;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
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
    private final String ENTER_ID = "Enter ID for book or 0 is your dont now ID";
    private final String GET_ALL = "Enter command 'getall' for search your book in list";

    private final BookDao bookDao;
    private final AuthorDao authorDao;

    private final ScannerHelper helper;

    public BookServiceImpl(BookDao bookDao, AuthorDao authorDao, AuthorDao authorDao1, ScannerHelper helper) {
        this.bookDao = bookDao;
        this.authorDao = authorDao1;
        this.helper = helper;
    }

    @Override
    public void getBook() {
        System.out.println(ENTER_ID);
        long id = helper.getIdFromUser();
        if (id > 0) {
            Book book = bookDao.getBookById(id);
            if (nonNull(book)) {
                System.out.println(book);
            } else {
                System.out.println("Book with this id is not exist");
            }
        } else {
            System.out.println(GET_ALL);
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
    public Book createBook() {
        Book book = new Book();
        System.out.println("Enter name of the book");
        String name = helper.getFromUser();
        System.out.println("Enter name of the author");
        String authorFromUser = helper.getAuthorsFromUser();
        Set<String> authors = Stream.of(authorFromUser.split(","))
                .map(String::new)
                .collect(Collectors.toSet());
        System.out.println("Enter years of publish");
        String yearStr = helper.getFromUser();
        val year = new YearOfPublish(yearStr);
        System.out.println("Enter name of the genre");
        String genreStr = helper.getFromUser();
        val genre = new Genre(genreStr);
        System.out.println("You can add comment to this book");
        val comment = new Comment(helper.getFromUser());
        book.setName(name);
        Set<Author> setAuthors = setAuthors(authors);
        for (var a : setAuthors) {
            book.addAuthors(a);
        }
        //todo fix non unique
        book.setYear(year);
        book.setGenre(genre);
        book.addComment(comment);
        return bookDao.createBook(book);
    }

    @Transactional
    @Override
    public void updateBook() {
        System.out.println(ENTER_ID);
        int id = helper.getIdFromUser();
        if (id > 0) {
            System.out.println("Enter correct name of the book");
            String name = helper.getFromUser();
            System.out.println("Enter correct name or names of the authors of the book");
            String authorFromUser = helper.getAuthorsFromUser();
            Set<String> authors = Stream.of(authorFromUser.split(","))
                    .map(String::new)
                    .collect(Collectors.toSet());
            Book bookFromDB = bookDao.getBookById(id);
            if (!name.isEmpty()) {
                bookFromDB.setName(name);
            }

            Set<Author> setAuthors = new HashSet<>();
            for (var author : authors) {
                try {
                    setAuthors.add(authorDao.getByName(author));
                } catch (RuntimeException e) {
                    setAuthors.add(new Author(author));
                }
            }
            bookFromDB.setAuthors(setAuthors);

            bookDao.updateBook(bookFromDB);
        } else {
            System.out.println(GET_ALL);
        }
    }

    @Override
    public void delBook() {
        System.out.println(ENTER_ID);
        int id = helper.getIdFromUser();
        if (id > 0) {
            bookDao.deleteBook(bookDao.getBookById(id));
            log.info("Book deleted successfully");
        } else {
            System.out.println(GET_ALL);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Set<Comment> getComments() {
        System.out.println(ENTER_ID);
        int id = helper.getIdFromUser();
        var comments = bookDao.getBookById(id).getComments();
        System.out.println(comments);
        return comments;
    }

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
}
