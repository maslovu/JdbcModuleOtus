package com.maslov.booksmaslov.service.impl;

import com.maslov.booksmaslov.domain.Book;
import com.maslov.booksmaslov.domain.Comment;
import com.maslov.booksmaslov.mapper.CommentMapper;
import com.maslov.booksmaslov.model.CommentDto;
import com.maslov.booksmaslov.model.CommentRequest;
import com.maslov.booksmaslov.repository.BookRepo;
import com.maslov.booksmaslov.repository.CommentRepo;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = CommentServiceImpl.class) // Нацеливаем контекст на сервис комментариев
class CommentServiceImplTest {

    @Autowired
    private CommentServiceImpl commentService;

    @MockBean
    private BookRepo bookRepo; // Изолируем репозиторий книг

    @MockBean
    private CommentRepo commentRepo;

    @MockBean
    private CommentMapper commentMapper; // Изолируем ваш кастомный маппер комментариев

    private Book mockBook;
    private CommentRequest commentRequest;
    private CommentDto expectedDto;
    private CommentRequest updateRequest;

    @BeforeEach
    void setUp() {
        // CommentRequest объявлен как record, создание через new CommentRequest("...")
        commentRequest = new CommentRequest("Отличная книга!");
        // Подготавливаем входящий запрос с новым текстом
        updateRequest = new CommentRequest("Обновленный текст комментария");

        mockBook = new Book();
        mockBook.setId(1L);
        mockBook.setTitle("Java core");

        reset(bookRepo, commentRepo, commentMapper); // Сбрасываем заглушки перед каждым тестом
    }

    @Test
    void getAllCommentForBook_WhenBookExists_ShouldReturnSetOfCommentDtos() {
        // Given
        long bookId = 1L;
        Comment comment1 = new Comment();
        comment1.setId(10L);
        comment1.setText("Хорошая книга");
        Comment comment2 = new Comment();
        comment2.setId(20L);
        comment2.setText("Полезный материал");
        // Инициализируем коллекцию комментариев внутри книги
        Set<Comment> commentsSet = new HashSet<>();
        commentsSet.add(comment1);
        commentsSet.add(comment2);
        mockBook.setComments(commentsSet);
        CommentDto dto1 = new CommentDto(10L, "Хорошая книга", 1L);
        CommentDto dto2 = new CommentDto(20L, "Полезный материал", 1L);

        // Настраиваем поведение изолированных бинов в контексте Spring
        when(bookRepo.getBookById(bookId)).thenReturn(Optional.of(mockBook));
        when(commentMapper.toDto(comment1)).thenReturn(dto1);
        when(commentMapper.toDto(comment2)).thenReturn(dto2);

        // When
        Set<CommentDto> result = commentService.getAllCommentForBook(bookId);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size(), "Должно вернуться ровно 2 комментария");

        // Верифицируем строгий вызов зависимостей
        verify(bookRepo, times(1)).getBookById(bookId);
        verify(commentMapper, times(1)).toDto(comment1);
        verify(commentMapper, times(1)).toDto(comment2);
    }

    @Test
    void getAllCommentForBook_WhenBookDoesNotExist_ShouldThrowEntityNotFoundException() {
        // Given
        long nonExistentBookId = 999L;
        when(bookRepo.getBookById(nonExistentBookId)).thenReturn(Optional.empty());

        // When & Then
        // Проверяем, что метод выбрасывает правильное стандартное JPA исключение
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            commentService.getAllCommentForBook(nonExistentBookId);
        });

        // Проверяем текст сообщения об ошибке
        assertEquals("Book not found with id: " + nonExistentBookId, exception.getMessage());

        // Верифицируем: репозиторий вызвался, а маппер полностью проигнорирован (трансформация не запускалась)
        verify(bookRepo, times(1)).getBookById(nonExistentBookId);
        verifyNoInteractions(commentMapper);
    }

    @Test
    void createComment_WhenBookExists_ShouldSaveAndReturnCommentDto() {
        // Given
        long bookId = 1L;
        Comment savedCommentFromDb = new Comment();
        savedCommentFromDb.setId(100L);
        savedCommentFromDb.setText("Отличная книга!");
        savedCommentFromDb.setBook(mockBook);
        // Ожидаемый результат после маппинга
        expectedDto = new CommentDto(100L, "Отличная книга!", 1L);

        // Настраиваем цепочку: нашли книгу -> сохранили комментарий -> смаппили в DTO
        when(bookRepo.getBookById(bookId)).thenReturn(Optional.of(mockBook));
        when(commentRepo.createComment(any(Comment.class))).thenReturn(savedCommentFromDb);
        when(commentMapper.toDto(savedCommentFromDb)).thenReturn(expectedDto);

        // When
        CommentDto result = commentService.createComment(commentRequest, bookId);

        // Then
        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals("Отличная книга!", result.getText());
        assertEquals(1L, result.getBookId());

        // Проверяем вызовы всех зависимостей по цепочке
        verify(bookRepo, times(1)).getBookById(bookId);
        verify(commentRepo, times(1)).createComment(any(Comment.class));
        verify(commentMapper, times(1)).toDto(savedCommentFromDb);
    }

    @Test
    void createComment_WhenBookDoesNotExist_ShouldThrowEntityNotFoundException() {
        // Given
        long nonExistentBookId = 999L;
        when(bookRepo.getBookById(nonExistentBookId)).thenReturn(Optional.empty());

        // When & Then
        // Проверяем, что метод выбрасывает правильное стандартное JPA исключение
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            commentService.createComment(commentRequest, nonExistentBookId);
        });

        // Проверяем корректность сообщения об ошибке
        assertEquals("Book not found with id: " + nonExistentBookId, exception.getMessage());

        // Верифицируем: поиск книги вызвался, а сохранение и маппинг проигнорированы
        verify(bookRepo, times(1)).getBookById(nonExistentBookId);
        verify(commentRepo, never()).createComment(any(Comment.class));
        verifyNoInteractions(commentMapper);
    }

    @Test
    void updateComment_WhenCommentExists_ShouldUpdateTextAndReturnDto() {
        // Given
        long commentId = 50L;
        expectedDto = new CommentDto(50L, "Обновленный текст комментария", 1L);
        // Существующий комментарий, который лежит в БД до обновления
        Comment existingComment = new Comment();
        existingComment.setId(50L);
        existingComment.setText("Старый текст комментария");
        existingComment.setBook(mockBook);

        // Настраиваем поведение моков
        when(commentRepo.getCommentById(commentId)).thenReturn(Optional.of(existingComment));
        when(commentMapper.toDto(existingComment)).thenReturn(expectedDto);

        // When
        CommentDto result = commentService.updateComment(updateRequest, commentId);

        // Then
        assertNotNull(result);
        assertEquals(50L, result.getId());
        assertEquals("Обновленный текст комментария", result.getText());

        // Верификация: текст внутри объекта existingComment действительно изменился
        assertEquals("Обновленный текст комментария", existingComment.getText());

        // Проверяем, что метод репозитория на сохранение/обновление НЕ вызывался (так как работает Dirty Checking)
        verify(commentRepo, times(1)).getCommentById(commentId);
        // Если у вас в интерфейсе есть метод updateComment, проверяем, что его не дергали:
        // verify(commentRepo, never()).updateComment(any());

        verify(commentMapper, times(1)).toDto(existingComment);
    }

    @Test
    void updateComment_WhenCommentDoesNotExist_ShouldThrowEntityNotFoundException() {
        // Given
        long nonExistentCommentId = 999L;
        when(commentRepo.getCommentById(nonExistentCommentId)).thenReturn(Optional.empty());

        // When & Then
        // Проверяем, что метод выбрасывает правильное исключение при отсутствии записи
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            commentService.updateComment(updateRequest, nonExistentCommentId);
        });

        // Проверяем корректность сообщения об ошибке
        assertEquals("Comment not found with id: " + nonExistentCommentId, exception.getMessage());

        // Верифицируем: поиск вызвался, а маппер полностью проигнорирован
        verify(commentRepo, times(1)).getCommentById(nonExistentCommentId);
        verifyNoInteractions(commentMapper);
    }

    @Test
    void deleteComment_WhenCommentExists_ShouldRemoveFromBookAndCallDelete() {
        // Given
        long commentId = 500L;
        Comment mockComment = new Comment();
        mockComment.setId(500L);
        mockComment.setText("Хороший комментарий");
        mockComment.setBook(mockBook);

        when(commentRepo.getCommentById(commentId)).thenReturn(Optional.of(mockComment));

        // When
        commentService.deleteComment(commentId);

        // Then
        // 1. Верифицируем, что комментарий успешно удалился из списка книги в памяти Java
        assertFalse(mockBook.getComments().contains(mockComment),
                "Комментарий должен быть удален из коллекции книги для синхронизации памяти");

        // 2. Проверяем, что методы репозитория были вызваны строго по одному разу
        verify(commentRepo, times(1)).getCommentById(commentId);
        verify(commentRepo, times(1)).deleteComment(mockComment);
    }

    @Test
    void deleteComment_WhenCommentDoesNotExist_ShouldThrowEntityNotFoundException() {
        // Given
        long nonExistentCommentId = 999L;
        when(commentRepo.getCommentById(nonExistentCommentId)).thenReturn(Optional.empty());

        // When & Then
        // Проверяем, что метод выбрасывает правильное исключение при отсутствии записи в БД
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            commentService.deleteComment(nonExistentCommentId);
        });

        // Проверяем корректность сообщения об ошибке
        assertEquals("Comment not found with id: " + nonExistentCommentId, exception.getMessage());

        // Верифицируем: поиск вызвался, а метод удаления репозитория не запускался
        verify(commentRepo, times(1)).getCommentById(nonExistentCommentId);
        verify(commentRepo, never()).deleteComment(any(Comment.class));
    }
}
