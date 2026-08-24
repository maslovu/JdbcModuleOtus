package com.maslov.booksmaslov.service.impl;

import com.maslov.booksmaslov.domain.Author;
import com.maslov.booksmaslov.domain.Book;
import com.maslov.booksmaslov.domain.Comment;
import com.maslov.booksmaslov.model.BookDto;
import com.maslov.booksmaslov.repository.BookRepo;
import com.maslov.booksmaslov.repository.impl.BookDaoImpl;
import com.maslov.booksmaslov.service.BookService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.HashSet;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@Import(BookDaoImpl.class)
@SpringJUnitConfig(BookServiceImpl.class)
class BookServiceImplTest {

    @MockBean
    private BookRepo bookRepo;

    @Autowired
    BookService service;

    @Test
    void getBook() {
        service.getBook(0);

        verify(bookRepo, Mockito.times(0)).getBookById(1);

    }

    @Test
    void createBook() {
        Set<Author> authors = new HashSet<>();
        authors.add(new Author("Gorky"));
        Set<Comment> comments = new HashSet<Comment>();
        comments.add(new Comment("Gorky"));
        BookDto bookDto = new BookDto("Gorky", "Gorky", "Gorky", "authors");
        Book book = new Book();

        service.createBook(bookDto);

        verify(bookRepo, Mockito.times(1)).createBook(book);
    }

//    @Test
//    void updateBook() {
//        Book book = new Book(1, "as", new Genre(), new YearOfPublish(), new HashSet<>(), new HashSet<>());
//
//        when(scanner.getIdFromUser()).thenReturn(1);
//        when(scanner.getFromUser()).thenReturn("anyString(");
//        when(scanner.getFromUser()).thenReturn("asadada");
//        when(scanner.getIdFromUser()).thenReturn(1);
//        when(bookDao.getBookById(1)).thenReturn(new Book());
//        when(bookDao.updateBook(new Book())).thenReturn(new Book());
//
//        service.updateBook();
//
//        verify(bookDao, Mockito.times(1))
//                .updateBook(any());
//    }

//    @Test
//    void delBook() {
//        when(scanner.getIdFromUser()).thenReturn(1);
//        when(bookDao.getBookById(1)).thenReturn(new Book());
//
//        service.delBook();
//
//        verify(bookDao, Mockito.times(1)).deleteBook(any());
//    }

//    @Test
//    void delBookWithZeroId() {
//        when(scanner.getIdFromUser()).thenReturn(0);
//
//        service.delBook();
//
//        verify(bookDao, Mockito.times(0)).deleteBook(any());
//    }
}