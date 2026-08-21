package com.maslov.booksmaslov.service.impl;

import com.maslov.booksmaslov.domain.Author;
import com.maslov.booksmaslov.domain.Book;
import com.maslov.booksmaslov.domain.Comment;
import com.maslov.booksmaslov.domain.Genre;
import com.maslov.booksmaslov.domain.YearOfPublish;
import com.maslov.booksmaslov.repository.BookDao;
import com.maslov.booksmaslov.service.BookService;
import com.maslov.booksmaslov.service.ScannerHelper;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
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

    private final ScannerHelper helper;

    public BookServiceImpl(BookDao bookDao, ScannerHelper helper) {
        this.bookDao = bookDao;
        this.helper = helper;
    }

    @Override
    public void getBook() {
        System.out.println(ENTER_ID);
        int id = helper.getIdFromUser();
        if (id > 0) {
            Book book = bookDao.getBookById(id).get();
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

    @Transactional
    @Override
    public Book createBook() {
        System.out.println("Enter name of the book");
        String name = helper.getFromUser();
        System.out.println("Enter name of the author");
        Author authorAr = new Author(0, helper.getFromUser());
        val author = Collections.singletonList(authorAr);
        System.out.println("Enter years of publish");
        String yearStr = helper.getFromUser();
        val year = new YearOfPublish(0, yearStr);
        System.out.println("Enter name of the genre");
        String genreStr = helper.getFromUser();
        val genre = new Genre(0, genreStr);
        System.out.println("You can add comment to this book");
        val comment = new Comment(0, helper.getFromUser());
        var comments = new HashSet<Comment>();
        comments.add(comment);
        return bookDao.createBook(new Book(0, name, genre, year, author, comments));
    }

    @Transactional
    @Override
    public void updateBook() {
        System.out.println(ENTER_ID);
        int id = helper.getIdFromUser();
        if (id > 0) {
            System.out.println("Enter correct name of the book");
            String name = helper.getFromUser();
            System.out.println("Enter correct name of the author");
            String author = helper.getFromUser();
            System.out.println("Enter correct author_id of the book");
            int authorId = helper.getIdFromUser();
            Book bookFromDB = bookDao.getBookById(id).orElseThrow();
            bookFromDB.setName(name);
            List<Author> authors = Stream.of(author.split(","))
                    .map(s -> new Author(authorId, s))
                    .collect(Collectors.toList());
            bookFromDB.setAuthor(authors);
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
            bookDao.deleteBook(bookDao.getBookById(id).orElseThrow());
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
        return bookDao.getBookById(id).orElseThrow().getListOfComments();
    }
}
