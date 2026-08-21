package com.maslov.booksmaslov.service.impl;

import com.maslov.booksmaslov.domain.Book;
import com.maslov.booksmaslov.repository.BookRepo;
import com.maslov.booksmaslov.service.BookService;
import com.maslov.booksmaslov.service.BookServiceHelper;
import com.maslov.booksmaslov.service.ScannerHelper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
//@Import({BookDao.class})
@SpringJUnitConfig(BookServiceImpl.class)
class BookServiceImplTest {

    private static final String TEST = "Test";
    @MockBean
    private ScannerHelper scanner;
    @MockBean
    private BookRepo bookRepo;
    @MockBean
    private BookServiceHelper bookServiceHelper;

    @Autowired
    BookService service;

    @Test
    void getBook() {

        when(scanner.getIdFromUser()).thenReturn(0);

        service.getBook();

        verify(bookRepo, Mockito.times(0)).findById(1L);

    }

    @Test
    void createBook() {

        when(bookServiceHelper.getBookFromUser(0)).thenReturn(Book.builder().name(TEST).build());

        service.createBook();

        verify(bookRepo, Mockito.times(1)).save(Book.builder().name(TEST).build());
    }

    @Test
    void updateBook() {
        when(scanner.getIdFromUser()).thenReturn(5);
        when(scanner.getEmptyString()).thenReturn("");
        when(bookRepo.findById(5L)).thenReturn(Optional.ofNullable(Book.builder().name(TEST).build()));

        service.updateBook();

        verify(bookRepo, Mockito.times(1))
                .save(any());
    }

    @Test
    void delBook() {
        when(scanner.getIdFromUser()).thenReturn(1);

        service.delBook();

        verify(bookRepo, Mockito.times(1)).deleteById(anyLong());
    }

    @Test
    void delBookWithZeroId() {
        when(scanner.getIdFromUser()).thenReturn(0);

        service.delBook();

        verify(bookRepo, Mockito.times(0)).deleteById(anyLong());
    }
}