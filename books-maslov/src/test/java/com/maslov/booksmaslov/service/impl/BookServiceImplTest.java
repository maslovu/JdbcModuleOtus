package com.maslov.booksmaslov.service.impl;

import com.maslov.booksmaslov.domain.Author;
import com.maslov.booksmaslov.domain.Book;
import com.maslov.booksmaslov.domain.Comment;
import com.maslov.booksmaslov.domain.Genre;
import com.maslov.booksmaslov.domain.YearOfPublish;
import com.maslov.booksmaslov.repository.BookDao;
import com.maslov.booksmaslov.repository.impl.BookDaoImpl;
import com.maslov.booksmaslov.service.BookService;
import com.maslov.booksmaslov.service.ScannerHelper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@Import(BookDaoImpl.class)
@SpringJUnitConfig(BookServiceImpl.class)
class BookServiceImplTest {

    @MockBean
    private ScannerHelper scanner;
    @MockBean
    private BookDao bookDao;

    @Autowired
    BookService service;

    @Test
    void getBook() {

        when(scanner.getIdFromUser()).thenReturn(0);

        service.getBook();

        verify(bookDao, Mockito.times(0)).getBookById(1);

    }

    @Test
    void createBook() {
        List<Author> authors = new ArrayList<>();
        authors.add(new Author(0, "Gorky"));
        Set<Comment> comments = new HashSet<Comment>();
        comments.add(new Comment(0, "Gorky"));
        Book book = new Book(0, "Gorky", new Genre(0, "Gorky"),
                new YearOfPublish(0, "Gorky"), authors, comments);

        when(scanner.getFromUser()).thenReturn("any()");
        when(scanner.getFromUser()).thenReturn("ex");
        when(scanner.getFromUser()).thenReturn("2020");
        when(scanner.getFromUser()).thenReturn("Gorky");

        service.createBook();

        verify(bookDao, Mockito.times(1)).createBook(book);
    }

    @Test
    void updateBook() {
        Book book = new Book(1, "as", new Genre(), new YearOfPublish(), new ArrayList<>(), new HashSet<>());

        when(scanner.getIdFromUser()).thenReturn(1);
        when(scanner.getFromUser()).thenReturn("anyString(");
        when(scanner.getFromUser()).thenReturn("asadada");
        when(scanner.getIdFromUser()).thenReturn(1);
        when(bookDao.getBookById(1)).thenReturn(Optional.of(new Book()));
        when(bookDao.updateBook(new Book())).thenReturn(new Book());

        service.updateBook();

        verify(bookDao, Mockito.times(1))
                .updateBook(any());
    }

    @Test
    void delBook() {
        when(scanner.getIdFromUser()).thenReturn(1);
        when(bookDao.getBookById(1)).thenReturn(Optional.of(new Book()));

        service.delBook();

        verify(bookDao, Mockito.times(1)).deleteBook(any());
    }

    @Test
    void delBookWithZeroId() {
        when(scanner.getIdFromUser()).thenReturn(0);

        service.delBook();

        verify(bookDao, Mockito.times(0)).deleteBook(any());
    }
}