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

    private final long TEST_ID = 42L;
    private BookDto bookDto;
    private Book bookEntity;
    private Book savedEntity;
    private Book freshCopyFromDb;
    private BookDto expectedDto;

    @BeforeEach
    void setUp() {
        bookDto = new BookDto(
                null,
                "Clean Code",
                "Robert Martin",
                1L,
                1L
        );
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

        expectedDto = new BookDto(42L, "Effective Java", "Author A, Author B", 2L, 1L);
    }

    @Test
    void getBook_WhenExists_ShouldReturnDto() {
        // Given
        given(bookRepo.findById(TEST_ID)).willReturn(Optional.of(bookEntity));

        BookDto expectedDto = new BookDto(TEST_ID, "Clean Code", "Robert Martin", 1980L, 1L);
        given(mapper.toDto(bookEntity)).willReturn(expectedDto);

        // When
        BookDto result = bookService.getBook(TEST_ID);

        // Then
        assertNotNull(result);
        assertEquals(TEST_ID, result.id());
        assertEquals("Clean Code", result.title());
        
        verify(bookRepo).findById(TEST_ID);
        verify(mapper).toDto(bookEntity);
    }

    @Test
    void getBook_WhenNotExists_ShouldThrowException() {
        // Given
        given(bookRepo.findById(TEST_ID)).willReturn(Optional.empty());

        // When & Then
        NoBookException exception = assertThrows(NoBookException.class, () -> { bookService.getBook(TEST_ID); });
        String expectedMessage = "Book with id " + TEST_ID + " does not exist";
        assertTrue(exception.getMessage().contains(expectedMessage));
    }

    @Test
    void getAllBook_WhenBooksExist_ReturnsListOfDtos() {
        // Given
        BookDto dto1 = new BookDto(1L, "Title 1", "Author 1", 2000L, 1L);
        BookDto dto2 = new BookDto(2L, "Title 2", "Author 2", 2010L, 2L);
        // Ожидаемый результат
        List<BookDto> expectedList = List.of(dto1, dto2);
        given(bookRepo.findAllBooks()).willReturn(expectedList);

        // When
        List<BookDto> result = bookService.getAllBook();

        // Then
        assertEquals(2, result.size());
        assertEquals("Title 1", result.get(0).title());
        assertEquals("Title 2", result.get(1).title());
    }

    @Test
    void getAllBook_WhenNoBooksExist_ReturnsEmptyList() {
        // Given
        given(bookRepo.findAllBooks()).willReturn(Collections.emptyList());

        // When
        List<BookDto> result = bookService.getAllBook();

        // Then
        assertTrue(result.isEmpty());
    }

    @Test void createBook_ValidInput_ReturnsSavedDto() {
        // Given
        given(mapper.toEntity(bookDto)).willReturn(bookEntity);
        given(bookRepo.save(bookEntity)).willReturn(savedEntity);
        given(bookRepo.findById(42L)).willReturn(Optional.of(freshCopyFromDb));

        given(mapper.toDto(freshCopyFromDb)).willReturn(expectedDto);

        // When
        BookDto result = bookService.createBook(bookDto);

        // Then
        assertNotNull(result);
        assertEquals(42L, result.id());
        assertEquals("Effective Java", result.title());

        verify(mapper).toEntity(bookDto);
        verify(bookRepo).save(bookEntity);
        verify(bookRepo).findById(42L);
    }

    @Test void createBook_WithAuthors_AuthorsAreProcessed() {
        // Given
        String authorsString = "Author A, Author B";
        BookDto dtoWithAuthors = new BookDto(null, "Clean Code", authorsString, 2L, 1L);

        bookEntity = new Book();
        bookEntity.setTitle("Effective Java");
        bookEntity.setAuthors(Set.of(new Author("Author A"), new Author("Author B")));

        given(mapper.toEntity(dtoWithAuthors)).willReturn(bookEntity);
        given(bookRepo.save(bookEntity)).willReturn(savedEntity);
        given(bookRepo.findById(42L)).willReturn(Optional.of(freshCopyFromDb));
        given(mapper.toDto(freshCopyFromDb)).willReturn(expectedDto);

        // When
        BookDto result = bookService.createBook(dtoWithAuthors);

        // Then
        assertNotNull(result);
        assertEquals(42L, result.id());

        verify(mapper, times(1)).toEntity(dtoWithAuthors);
        verify(bookRepo, times(1)).save(bookEntity);
        verify(bookRepo, times(1)).findById(42L);
        verify(mapper, times(1)).toDto(freshCopyFromDb);
    }

    @Test
    void createBook_DatabaseThrowsException_TransWhenionRollsBack() {
        // Given
        given(mapper.toEntity(bookDto)).willReturn(bookEntity);

        given(bookRepo.save(bookEntity)).willThrow(new RuntimeException("DB Error"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookService.createBook(bookDto); });

        assertTrue(exception.getMessage().contains("DB Error"));
        verify(bookRepo, never()).findById(anyLong());
    }

    @Test
    void updateBook_ValidInput_ReturnsUpdatedDto() {
        // Given
        long id = 42L;

        Book existingBook = new Book();
        existingBook.setId(id);
        existingBook.setTitle("Old Title");

        BookDto inputDto = new BookDto(null, "New Title", "New Author", 2024L, 2L);

        BookDto expectedOutput = new BookDto(42L, "New Title", "New Author", 2024L, 2L);

        given(bookRepo.findById(id)).willReturn(Optional.of(existingBook));
        given(mapper.toDto(existingBook)).willReturn(expectedOutput);

        // When
        BookDto result = bookService.updateBook(id, inputDto);

        // Then
        assertEquals("New Title", result.title());
        assertEquals(2024L, result.yearId());

        verify(bookRepo).findById(id);
        verify(mapper).updateEntityFromDto(inputDto, existingBook);
    }

    @Test
    void updateBook_VerifiesFieldsChangedInEntityBeforeSaving() {
        // Given
        long id = 1L;
        Book existingBook = new Book();
        existingBook.setId(id);
        existingBook.setTitle("Old");

        BookDto dto = new BookDto(null, "Brand New Title", "null", 2L, 1L);
        given(bookRepo.findById(id)).willReturn(Optional.of(existingBook));

        doAnswer(invocation -> {
            Book book = invocation.getArgument(1);
            book.setTitle(dto.title());
            return null;
        }).when(mapper).updateEntityFromDto(eq(dto), any(Book.class));

        given(mapper.toDto(any(Book.class))).willReturn(dto);

        // When
        bookService.updateBook(id, dto);

        // Then: Используем захват аргумента для mapper'а
        ArgumentCaptor<Book> entityCaptor = ArgumentCaptor.forClass(Book.class);

        verify(mapper).updateEntityFromDto(eq(dto), entityCaptor.capture());

        Book capturedEntity = entityCaptor.getValue();

        assertEquals("Brand New Title", capturedEntity.getTitle());
    }

    @Test
    void updateBook_IdNotFound_ShouldThrowNoBookException() {
        // Given
        long missingId = 999L;
        BookDto anyDto = new BookDto(null, "X", "Y", 2000L, 1L);

        given(bookRepo.findById(missingId)).willReturn(Optional.empty());

        // When & Then
        NoBookException ex = assertThrows(NoBookException.class, () -> { bookService.updateBook(missingId, anyDto);});

        assertEquals("Book with id 999 does not exist", ex.getMessage());

        verify(mapper, never()).updateEntityFromDto(any(), any()); // Маппер даже не должен был вызваться
    }

    @Test
    void delBook_WhenBookExists_CallsRepoDelete() {

        // Given
        long existingId = 42L;

        // When
        bookService.delBook(existingId);

        // Then
        verify(bookRepo, times(1)).deleteById(existingId);
    }

    @Test void delBook_WhenBookDoesNotExist_DoesNotThrowException() {

        // Given
        long missingId = 999L;

        // When & Then
        assertDoesNotThrow(() -> {
            bookService.delBook(missingId);
        });

        // Дополнительно убеждаемся, что попытка удаления всё же была предпринята
        verify(bookRepo).deleteById(missingId); }
}
