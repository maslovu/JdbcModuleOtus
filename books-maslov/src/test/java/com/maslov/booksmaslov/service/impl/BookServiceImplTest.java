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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = BookServiceImpl.class)
//@SpringJUnitConfig(BookServiceImpl.class)
class BookServiceImplTest {

    @Autowired
    private BookServiceImpl bookService; // Внедряем тестируемый сервис из контекста

    @MockBean
    private BookRepo bookRepo;

    @MockBean
    private YearRepo yearRepo;

    @MockBean
    private GenreRepo genreRepo;

    @MockBean
    private AuthorRepo authorRepo;

    @MockBean
    private BookMapper mapper;

    private BookDto inputDto;
    private BookDto expectedDto;
    private Author mockAuthor;
    private Genre mockGenre;
    private Book mockBookFromDb;

    @Autowired
    BookService service;


    @BeforeEach
    void setUp() {
        inputDto = new BookDto();
        inputDto.setTitle("Java core");
        inputDto.setAuthors("Lafore");
        inputDto.setYear("2025");
        inputDto.setGenre("Non Fiction");

        expectedDto = new BookDto();
        expectedDto.setTitle("Java core");
        expectedDto.setAuthors("Lafore");
        expectedDto.setYear("2025");
        expectedDto.setGenre("Non Fiction");

        // Инициализируем базовые объекты заглушек
        mockAuthor = new Author();
        mockAuthor.setName("Lafore");
        mockAuthor.setBooks(new HashSet<>()); // Защита от NullPointerException в addAuthors

        mockBookFromDb = new Book();
        mockBookFromDb.setTitle("Java core");

        mockGenre = new Genre("Non Fiction");

        reset(authorRepo, yearRepo, genreRepo, mapper);
    }

    @Test
    void getBookWithException() {
        var ex = assertThrows(NoBookException.class, () -> {
            service.getBook(0);
        });

        assertEquals("Book is not exist", ex.getMessage());
    }

    @Test
    void createBook_WhenYearAndGenreExist_ShouldReturnSavedBookDto() {
        // Given
        YearOfPublish existingYear = new YearOfPublish("2025");
        // Настройка заглушек для внутренних хелперов метода (getAuthorsSet, getYear, getGenre)
        // Предполагается, что getAuthorsSet ищет авторов в базе. Задайте имя метода вашего репозитория авторов:
        when(authorRepo.getByName("Lafore")).thenReturn(Optional.of(mockAuthor));
        when(yearRepo.getYearByDate("2025")).thenReturn(Optional.of(existingYear));
        when(genreRepo.getGenreByName("Non Fiction")).thenReturn(Optional.of(mockGenre));

        // Настройка для @MockBean bookRepo
        when(bookRepo.createBook(any(Book.class))).thenReturn(mockBookFromDb);
        when(mapper.toDto(any(Book.class))).thenReturn(expectedDto);

        // When
        BookDto result = bookService.createBook(inputDto);

        // Then
        assertNotNull(result);
        assertEquals("Java core", result.getTitle());
        assertEquals("2025", result.getYear());

        // Проверяем, что новые объекты года и жанра НЕ создавались
        verify(yearRepo, never()).createYear(any(YearOfPublish.class));
        verify(genreRepo, never()).createGenre(any(Genre.class));
    }

    @Test
    void createBook_WhenYearAndGenreDoNotExist_ShouldCreateThemAndReturnBookDto() {
        // Given
        YearOfPublish newYear = new YearOfPublish("2025");
        Genre newGenre = new Genre("Non Fiction");

        // Настраиваем сценарий, когда в базе ничего нет (Optional.empty())
        when(authorRepo.getByName("Lafore")).thenReturn(Optional.of(mockAuthor));
        when(yearRepo.getYearByDate("2025")).thenReturn(Optional.empty());
        when(genreRepo.getGenreByName("Non Fiction")).thenReturn(Optional.empty());

        // Настраиваем методы сохранения репозиториев, которые вызываются внутри orElseGet
        when(yearRepo.createYear(any(YearOfPublish.class))).thenReturn(newYear);
        when(genreRepo.createGenre(any(Genre.class))).thenReturn(newGenre);

        // Настройка для @MockBean bookRepo
        when(bookRepo.createBook(any(Book.class))).thenReturn(mockBookFromDb);
        when(mapper.toDto(any(Book.class))).thenReturn(expectedDto);

        // When
        BookDto result = bookService.createBook(inputDto);

        // Then
        assertNotNull(result);
        assertEquals("Java core", result.getTitle());
        assertEquals("2025", result.getYear());

        // Проверяем, что из-за отсутствия записей сработали методы создания
        verify(yearRepo, times(1)).createYear(any(YearOfPublish.class));
        verify(genreRepo, times(1)).createGenre(any(Genre.class));
    }
}