//package com.maslov.booksmaslov.service.impl;
//
//import com.maslov.booksmaslov.domain.Book;
//import com.maslov.booksmaslov.exception.NoBookException;
//import com.maslov.booksmaslov.mapper.BookMapper;
//import com.maslov.booksmaslov.model.BookDto;
//import com.maslov.booksmaslov.repository.BookRepo;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.dao.DataIntegrityViolationException;
//
//import java.util.Collections;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.never;
//import static org.mockito.Mockito.reset;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.verifyNoInteractions;
//import static org.mockito.Mockito.when;
//
//@SpringBootTest(classes = BookServiceImpl.class)
//class BookServiceImplTest {
//    @Autowired
//    private BookServiceImpl bookService;
//
//    @MockBean
//    private BookRepo bookRepo;
//
//    @MockBean
//    private BookMapper mapper;
//
//    private BookDto inputDto;
//    private Book mockBook;
//    private BookDto expectedDto;
//
//    @BeforeEach
//    void setUp() {
//        inputDto = new BookDto();
//        inputDto.setTitle("Java core");
//        inputDto.setAuthors("Lafore");
//        inputDto.setYear("2025");
//        inputDto.setGenre("Non Fiction");
//
//        mockBook = new Book();
//        mockBook.setId(42L);
//        mockBook.setTitle("Java core");
//
//        expectedDto = new BookDto();
//        expectedDto.setId(42L);
//        expectedDto.setTitle("Java core");
//        expectedDto.setAuthors("Lafore");
//        expectedDto.setYear("2025");
//        expectedDto.setGenre("Non Fiction");
//
//        reset(bookRepo, mapper); // Очищаем заглушки перед каждым тестом
//    }
//
//    @Test
//    void getBook_WhenBookExists_ShouldReturnBookDto() {
//        // Given
//        long bookId = 1L;
//
//        // Настраиваем поведение моков: репозиторий возвращает заполненный Optional
//        when(bookRepo.getBookById(bookId)).thenReturn(Optional.of(mockBook));
//        when(mapper.toDto(mockBook)).thenReturn(expectedDto);
//
//        // When
//        BookDto result = bookService.getBook(bookId);
//
//        // Then
//        assertNotNull(result);
//        assertEquals("Java core", result.getTitle());
//        assertEquals("2025", result.getYear());
//
//        // Проверяем, что методы зависимостей вызывались ровно по одному разу
//        verify(bookRepo, times(1)).getBookById(bookId);
//        verify(mapper, times(1)).toDto(mockBook);
//    }
//
//    @Test
//    void getBook_WhenBookDoesNotExist_ShouldThrowNoBookException() {
//        // Given
//        long nonExistentId = 999L;
//
//        // Настраиваем поведение моков: репозиторий возвращает пустой Optional
//        when(bookRepo.getBookById(nonExistentId)).thenReturn(Optional.empty());
//
//        // When & Then
//        // Используем assertThrows для перехвата и проверки вашего кастомного исключения
//        NoBookException exception = assertThrows(NoBookException.class, () -> {
//            bookService.getBook(nonExistentId);
//        });
//
//        // Проверяем текст сообщения внутри исключения
//        assertEquals("Book is not exist", exception.getMessage());
//
//        // Верифицируем: к мапперу дело даже не дошло, репозиторий вызвался
//        verify(bookRepo, times(1)).getBookById(nonExistentId);
//        verifyNoInteractions(mapper); // Маппер не должен вызываться для пустой книги
//    }
//
//    @Test
//    void getAllBook_WhenBooksExist_ShouldReturnListOfBookDtos() {
//        // Given
//        Book book1 = new Book();
//        book1.setId(1L);
//        book1.setTitle("Java core");
//
//        Book book2 = new Book();
//        book2.setId(2L);
//        book2.setTitle("Effective Java");
//
//        BookDto dto1 = new BookDto();
//        dto1.setId(1L);
//        dto1.setTitle("Java core");
//
//        BookDto dto2 = new BookDto();
//        dto2.setId(2L);
//        dto2.setTitle("Effective Java");
//
//        // Настраиваем моки: репозиторий возвращает список из 2 книг
//        when(bookRepo.getAllBooks()).thenReturn(List.of(book1, book2));
//        when(mapper.toDto(book1)).thenReturn(dto1);
//        when(mapper.toDto(book2)).thenReturn(dto2);
//
//        // When
//        List<BookDto> result = bookService.getAllBook();
//
//        // Then
//        assertNotNull(result);
//        assertEquals(2, result.size(), "Должно вернуться ровно 2 книги");
//        assertEquals("Java core", result.get(0).getTitle());
//        assertEquals("Effective Java", result.get(1).getTitle());
//
//        // Проверяем взаимодействие с зависимостями
//        verify(bookRepo, times(1)).getAllBooks();
//        verify(mapper, times(1)).toDto(book1);
//        verify(mapper, times(1)).toDto(book2);
//    }
//
//    @Test
//    void getAllBook_WhenNoBooksInDb_ShouldReturnEmptyList() {
//        // Given
//        // Настраиваем репозиторий на возврат пустого списка
//        when(bookRepo.getAllBooks()).thenReturn(Collections.emptyList());
//
//        // When
//        List<BookDto> result = bookService.getAllBook();
//
//        // Then
//        assertNotNull(result);
//        assertTrue(result.isEmpty(), "Список должен быть пуст, если в БД нет книг");
//
//        // Верифицируем: репозиторий вызвался, а маппер ни разу не дергался (так как цикл не запустился)
//        verify(bookRepo, times(1)).getAllBooks();
//        verifyNoInteractions(mapper);
//    }
//
//    @Test
//    void createBook_ShouldMapSaveAndReturnDto() {
//        // Given
//        Book savedBookFromDb = new Book();
//        savedBookFromDb.setId(42L);
//        savedBookFromDb.setTitle("Java core");
//
//        // Настраиваем цепочку вызовов: DTO -> Entity -> Save -> SavedEntity -> ReturnDTO
//        when(mapper.toEntity(inputDto)).thenReturn(mockBook);
//        when(bookRepo.createBook(mockBook)).thenReturn(savedBookFromDb);
//        when(mapper.toDto(savedBookFromDb)).thenReturn(expectedDto);
//
//        // When
//        BookDto result = bookService.createBook(inputDto);
//
//        // Then
//        assertNotNull(result);
//        assertEquals(42L, result.getId());
//        assertEquals("Java core", result.getTitle());
//
//        // Верифицируем строгую последовательность шагов бизнес-логики
//        verify(mapper, times(1)).toEntity(inputDto);
//        verify(bookRepo, times(1)).createBook(mockBook);
//        verify(mapper, times(1)).toDto(savedBookFromDb);
//    }
//
//    @Test
//    void createBook_WhenRepositoryThrowsException_ShouldPropagateException() {
//        // Given
//        when(mapper.toEntity(inputDto)).thenReturn(mockBook);
//
//        // Симулируем ошибку базы данных (например, нарушение констреинта UNIQUE)
//        when(bookRepo.createBook(mockBook)).thenThrow(new DataIntegrityViolationException("Database error"));
//
//        // When & Then
//        // Убеждаемся, что сервис пробрасывает ошибку наверх для отката транзакции
//        assertThrows(DataIntegrityViolationException.class, () -> {
//            bookService.createBook(inputDto);
//        });
//
//        // Верифицируем: до финального маппинга в DTO код даже не дошел
//        verify(mapper, times(1)).toEntity(inputDto);
//        verify(bookRepo, times(1)).createBook(mockBook);
//        verify(mapper, never()).toDto(any());
//    }
//
//    @Test
//    void updateBook_WhenBookExists_ShouldMapUpdateAndReturnDto() {
//        // Given
//        long bookId = 1L;
//        inputDto = new BookDto();
//        inputDto.setTitle("Java core (Updated)");
//        inputDto.setAuthors("Lafore");
//        expectedDto = new BookDto();
//        expectedDto.setId(1L);
//        expectedDto.setTitle("Java core (Updated)");
//        Book existingBook = new Book();
//        existingBook.setId(1L);
//        existingBook.setTitle("Java core");
//        Book updatedBookFromDb = new Book();
//        updatedBookFromDb.setId(1L);
//        updatedBookFromDb.setTitle("Java core (Updated)");
//
//        // Настраиваем цепочку: нашли книгу -> смаппили изменения сверху -> сохранили -> вернули DTO
//        when(bookRepo.getBookById(bookId)).thenReturn(Optional.of(existingBook));
//        when(mapper.toEntity(inputDto, existingBook)).thenReturn(updatedBookFromDb);
//        when(bookRepo.updateBook(updatedBookFromDb)).thenReturn(updatedBookFromDb);
//        when(mapper.toDto(updatedBookFromDb)).thenReturn(expectedDto);
//
//        // When
//        BookDto result = bookService.updateBook(bookId, inputDto);
//
//        // Then
//        assertNotNull(result);
//        assertEquals("Java core (Updated)", result.getTitle());
//
//        // Проверяем вызовы всех зависимостей по цепочке
//        verify(bookRepo, times(1)).getBookById(bookId);
//        verify(mapper, times(1)).toEntity(inputDto, existingBook);
//        verify(bookRepo, times(1)).updateBook(updatedBookFromDb);
//        verify(mapper, times(1)).toDto(updatedBookFromDb);
//    }
//
//    @Test
//    void updateBook_WhenBookDoesNotExist_ShouldThrowException() {
//        // Given
//        long nonExistentId = 999L;
//        when(bookRepo.getBookById(nonExistentId)).thenReturn(Optional.empty());
//
//        // When & Then
//        // Проверяем, что если книги нет, выполнение прерывается
//        assertThrows(NoBookException.class, () -> {
//            bookService.updateBook(nonExistentId, inputDto);
//        });
//
//        // Верифицируем: репозиторий поиска вызвался, а маппинг и сохранение проигнорированы
//        verify(bookRepo, times(1)).getBookById(nonExistentId);
//        verify(mapper, never()).toEntity(any(), any());
//        verify(bookRepo, never()).updateBook(any());
//        verify(mapper, never()).toDto(any());
//    }
//
//    @Test
//    void delBook_WhenBookExists_ShouldDeleteSuccessfully() {
//        // Given
//        long bookId = 1L;
//
//        // Настраиваем мок: книга успешно найдена в базе данных
//        when(bookRepo.getBookById(bookId)).thenReturn(Optional.of(mockBook));
//
//        // When
//        bookService.delBook(bookId);
//
//        // Then
//        // Верифицируем, что метод поиска и метод удаления вызваны строго по 1 разу
//        verify(bookRepo, times(1)).getBookById(bookId);
//        verify(bookRepo, times(1)).deleteBook(mockBook);
//    }
//
//    @Test
//    void delBook_WhenBookDoesNotExist_ShouldThrowNoBookException() {
//        // Given
//        long nonExistentId = 999L;
//
//        // Настраиваем мок: репозиторий возвращает пустой Optional
//        when(bookRepo.getBookById(nonExistentId)).thenReturn(Optional.empty());
//
//        // When & Then
//        // Проверяем, что метод выбрасывает именно NoBookException
//        NoBookException exception = assertThrows(NoBookException.class, () -> {
//            bookService.delBook(nonExistentId);
//        });
//
//        // Проверяем корректность сообщения внутри исключения
//        assertEquals("Book with id " + nonExistentId + " does not exist", exception.getMessage());
//
//        // Верифицируем: поиск вызвался, а метод удаления не дергался (выполнение прервалось)
//        verify(bookRepo, times(1)).getBookById(nonExistentId);
//        verify(bookRepo, never()).deleteBook(any(Book.class));
//    }
//}
