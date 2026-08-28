package com.maslov.booksmaslov.service.impl;

import com.maslov.booksmaslov.domain.Author;
import com.maslov.booksmaslov.domain.Book;
import com.maslov.booksmaslov.dto.BookDto;
import com.maslov.booksmaslov.exception.NoBookException;
import com.maslov.booksmaslov.mapper.BookMapper;
import com.maslov.booksmaslov.repository.BookRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

//Я не использовал @SpringBootTest (Unit Test),
// потому что для юнит-тестирования конкретного класса это считается «антипаттерном» и избыточностью
@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Mock
    private BookRepo bookRepo;

    @Mock
    private BookMapper mapper;

    @InjectMocks
    private BookServiceImpl bookService;

    // Тестовые данные (Arrange), которые пересоздаются перед каждым тестом
    private final long TEST_ID = 42L;
    private BookDto bookDto;
    private Book bookEntity;
    // Входящие данные от клиента
    private Book savedEntity;
    private Book freshCopyFromDb;
    // Ожидаемый результат
    private BookDto expectedDto;

    @BeforeEach
    void setUp() {
        // Инициализация Record (BookDto). Поля должны соответствовать сигнатуре record'а.
        bookDto = new BookDto(
                null,// id - нет на входе при создании
                "Clean Code", // title
                "Robert Martin", // authors (строка из условия)
                1L, // yearId
                1L // genreId
        );
        // Инициализация Entity
        bookEntity = new Book();
        bookEntity.setId(TEST_ID);
        bookEntity.setTitle("Clean Code");

        savedEntity = new Book();
        savedEntity.setId(42L);
        savedEntity.setTitle("Effective Java");
        savedEntity.setAuthors(Set.of(new Author("Test")));

        freshCopyFromDb = new Book();
        freshCopyFromDb.setId(42L);
        freshCopyFromDb.setTitle("Effective Java");
        freshCopyFromDb.setAuthors(Set.of(new Author("Test")));

        // Ожидаемый DTO для финала
        expectedDto = new BookDto(42L, "Effective Java", "Author A, Author B", 2L, 1L);
    }

    @Test
    void getBook_WhenExists_ShouldReturnDto() {
        // Arrange: настраиваем поведение зависимостей
        given(bookRepo.findById(TEST_ID)).willReturn(Optional.of(bookEntity));

        // Ожидаемый результат после работы маппера
        BookDto expectedDto = new BookDto(TEST_ID, "Clean Code", "Robert Martin", 1980L, 1L);
        given(mapper.toDto(bookEntity)).willReturn(expectedDto);

        // Act: вызываем метод сервиса
        BookDto result = bookService.getBook(TEST_ID);

        // Assert: проверяем результат
        assertNotNull(result);
        assertEquals(TEST_ID, result.id());
        assertEquals("Clean Code", result.title());

        // Проверка взаимодействия (убедимся, что методы вызвались)
        verify(bookRepo).findById(TEST_ID);
        verify(mapper).toDto(bookEntity);
    }

    @Test
    void getBook_WhenNotExists_ShouldThrowException() {
        // Arrange: репозиторий возвращает пустой Optional
        given(bookRepo.findById(TEST_ID)).willReturn(Optional.empty());

        // Act & Assert: проверяем выброс исключения
        NoBookException exception = assertThrows(NoBookException.class, () -> { bookService.getBook(TEST_ID); });
        String expectedMessage = "Book with id " + TEST_ID + " does not exist";
        assertTrue(exception.getMessage().contains(expectedMessage));
    }

    @Test
    void getAllBook_WhenBooksExist_ReturnsListOfDtos() {
        // Arrange: Специфичная настройка только для этого сценария
        BookDto dto1 = new BookDto(1L, "Title 1", "Author 1", 2000L, 1L);
        BookDto dto2 = new BookDto(2L, "Title 2", "Author 2", 2010L, 2L);
        // Ожидаемый результат
        List<BookDto> expectedList = List.of(dto1, dto2);
        given(bookRepo.findAllBooks()).willReturn(expectedList);

        // Act
        List<BookDto> result = bookService.getAllBook();

        // Assert
        assertEquals(2, result.size());
        assertEquals("Title 1", result.get(0).title());
        assertEquals("Title 2", result.get(1).title());
    }

    @Test
    void getAllBook_WhenNoBooksExist_ReturnsEmptyList() {
        // Arrange: Другое поведение для другого сценария
        given(bookRepo.findAllBooks()).willReturn(Collections.emptyList());

        // Act
        List<BookDto> result = bookService.getAllBook();

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test void createBook_ValidInput_ReturnsSavedDto() {
        // Arrange: Настраиваем поведение цепочки вызовов внутри сервиса
        given(mapper.toEntity(bookDto)).willReturn(bookEntity);
        given(bookRepo.save(bookEntity)).willReturn(savedEntity);
        given(bookRepo.findById(42L)).willReturn(Optional.of(freshCopyFromDb));

        given(mapper.toDto(freshCopyFromDb)).willReturn(expectedDto);

        // Act
        BookDto result = bookService.createBook(bookDto);

        // Assert
        assertNotNull(result);
        assertEquals(42L, result.id());
        assertEquals("Effective Java", result.title());

        // Verify: Убеждаемся, что методы вызвались правильно
        verify(mapper).toEntity(bookDto);
        verify(bookRepo).save(bookEntity);
        verify(bookRepo).findById(42L);
    }

    @Test void createBook_WithAuthors_AuthorsAreProcessed() {
        // Arrange
        String authorsString = "Author A, Author B";
        BookDto dtoWithAuthors = new BookDto(null, "Clean Code", authorsString, 2L, 1L);

        bookEntity = new Book();
        bookEntity.setTitle("Effective Java");
        bookEntity.setAuthors(Set.of(new Author("Author A"), new Author("Author B")));

        given(mapper.toEntity(dtoWithAuthors)).willReturn(bookEntity);
        given(bookRepo.save(bookEntity)).willReturn(savedEntity);
        given(bookRepo.findById(42L)).willReturn(Optional.of(freshCopyFromDb));
        given(mapper.toDto(freshCopyFromDb)).willReturn(expectedDto);

        // Act
        BookDto result = bookService.createBook(dtoWithAuthors);

        // Assert
        assertNotNull(result);
        assertEquals(42L, result.id());

        // Верифицируем, что сервис строго по цепочке передал объект от маппера в репозиторий
        verify(mapper, times(1)).toEntity(dtoWithAuthors);
        verify(bookRepo, times(1)).save(bookEntity);
        verify(bookRepo, times(1)).findById(42L);
        verify(mapper, times(1)).toDto(freshCopyFromDb);
    }

    @Test
    void createBook_DatabaseThrowsException_TransactionRollsBack() {
        // Arrange
        given(mapper.toEntity(bookDto)).willReturn(bookEntity);

        // Эмулируем падение БД (например, дубликат ключа)
        given(bookRepo.save(bookEntity)).willThrow(new RuntimeException("DB Error"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookService.createBook(bookDto); });

        assertTrue(exception.getMessage().contains("DB Error"));
        // Важно: findById не должен вызываться, так как save упал
        verify(bookRepo, never()).findById(anyLong());
    }

    @Test
    void updateBook_ValidInput_ReturnsUpdatedDto() {
        // Arrange
        long id = 42L;

        // Данные, которые сейчас лежат в базе
        Book existingBook = new Book();
        existingBook.setId(id);
        existingBook.setTitle("Old Title");

        // Новые данные от клиента
        BookDto inputDto = new BookDto(null, "New Title", "New Author", 2024L, 2L);

        // Ожидаемый результат
        BookDto expectedOutput = new BookDto(42L, "New Title", "New Author", 2024L, 2L);

        given(bookRepo.findById(id)).willReturn(Optional.of(existingBook));
        given(mapper.toDto(existingBook)).willReturn(expectedOutput);

        // Act
        BookDto result = bookService.updateBook(id, inputDto);

        // Assert
        assertEquals("New Title", result.title());
        assertEquals(2024L, result.yearId());

        verify(bookRepo).findById(id);
        verify(mapper).updateEntityFromDto(inputDto, existingBook);
    }

    @Test
    void updateBook_VerifiesFieldsChangedInEntityBeforeSaving() {
        // Arrange
        long id = 1L;
        Book existingBook = new Book();
        existingBook.setId(id);
        existingBook.setTitle("Old");

        BookDto dto = new BookDto(null, "Brand New Title", "null", 2L, 1L);
        given(bookRepo.findById(id)).willReturn(Optional.of(existingBook));

        // СИМУЛЯЦИЯ: Говорим моку маппера при вызове метода взять второй аргумент (Book)
        // и принудительно установить ему новое название из DTO
        doAnswer(invocation -> {
            Book book = invocation.getArgument(1); // Достаем @MappingTarget Book
            book.setTitle(dto.title());
            return null; // Метод void, возвращаем null
        }).when(mapper).updateEntityFromDto(eq(dto), any(Book.class));

        given(mapper.toDto(any(Book.class))).willReturn(dto);

        // Act
        bookService.updateBook(id, dto);

        // Assert: Используем захват аргумента для mapper'а
        ArgumentCaptor<Book> entityCaptor = ArgumentCaptor.forClass(Book.class);

        verify(mapper).updateEntityFromDto(eq(dto), entityCaptor.capture());

        Book capturedEntity = entityCaptor.getValue();

        // Проверяем состояние объекта ПОСЛЕ работы маппера
        assertEquals("Brand New Title", capturedEntity.getTitle());
    }

    @Test
    void updateBook_IdNotFound_ShouldThrowNoBookException() {
        // Arrange
        long missingId = 999L;
        BookDto anyDto = new BookDto(null, "X", "Y", 2000L, 1L);

        given(bookRepo.findById(missingId)).willReturn(Optional.empty());

        // Act & Assert
        NoBookException ex = assertThrows(NoBookException.class, () -> { bookService.updateBook(missingId, anyDto);});

        assertEquals("Book with id 999 does not exist", ex.getMessage());

        verify(mapper, never()).updateEntityFromDto(any(), any()); // Маппер даже не должен был вызваться
    }

    @Test
    void delBook_WhenBookExists_CallsRepoDelete() {

        // Arrange
        long existingId = 42L;

        // Act
        bookService.delBook(existingId);

        // Assert
        verify(bookRepo, times(1)).deleteById(existingId);
        // Проверяем, что метод вызван ровно один раз с нужным аргументом
    }

    @Test void delBook_WhenBookDoesNotExist_DoesNotThrowException() {

        // Arrange
        long missingId = 999L;

        // В Mock-репозитории deleteById по умолчанию ничего не делает (Void).
        // Нам НЕ нужно писать given(...).willThrow(...) специально,
        // мы хотим проверить поведение ПО УМОЛЧАНИЮ.

        // Act & Assert
        // JUnit 5 считает тест проваленным, если внутри него вылетает любое Exception.
        // Просто вызываем метод. Если он упал - тест красный. Если промолчал - зеленый.

        assertDoesNotThrow(() -> {
            bookService.delBook(missingId);
        });

        // Дополнительно убеждаемся, что попытка удаления всё же была предпринята
        verify(bookRepo).deleteById(missingId); }
}
