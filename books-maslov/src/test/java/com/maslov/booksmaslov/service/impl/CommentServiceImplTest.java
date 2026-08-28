package com.maslov.booksmaslov.service.impl;

import com.maslov.booksmaslov.domain.Book;
import com.maslov.booksmaslov.domain.Comment;
import com.maslov.booksmaslov.dto.CommentDto;
import com.maslov.booksmaslov.dto.CommentRequest;
import com.maslov.booksmaslov.mapper.CommentMapper;
import com.maslov.booksmaslov.repository.BookRepo;
import com.maslov.booksmaslov.repository.CommentRepo;
import com.maslov.booksmaslov.service.CommentService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock
    private BookRepo bookRepo;

    @Mock
    private CommentRepo commentRepo;

    @Mock
    private CommentMapper mapper;

    @InjectMocks
    private CommentServiceImpl commentService;

    // === Переменные для подготовки данных (@BeforeEach) ===
    private long bookId;
    private String commentText;

    private CommentRequest requestDto;
    private Book bookEntity;
    private Comment savedComment; // То, что вернет репозиторий после save
    private CommentDto expectedOutput;

    @BeforeEach
    void setUp() {
        // Инициализация примитивов и простых объектов
        bookId = 42L;
        commentText = "Great read!";

        // Входящий запрос от клиента
        requestDto = new CommentRequest(commentText);

        // Сущности-заглушки
        bookEntity = new Book();
        bookEntity.setId(bookId);
        savedComment = new Comment(commentText);
        savedComment.setId(100L); // База присвоила ID
        savedComment.setBook(bookEntity); // Устанавливаем связь вручную для теста

        // Ожидаемый результат на выходе
        expectedOutput = new CommentDto(100L, commentText, 1L);
    }

    @Test
    void getAllCommentForBook_WhenCommentsExist_ReturnsListOfDtos() {
        // Arrange
        long bookId = 42L;

        // Заглушки данных из БД
        Comment c1 = new Comment("Nice!");
        Comment c2 = new Comment("Bad...");

        List<Comment> dbResponse = List.of(c1, c2);

        // Ожидаемые DTO на выходе
        CommentDto dto1 = new CommentDto(1L, "Nice!", 3L);
        CommentDto dto2 = new CommentDto(2L, "Bad...", 3L);

        List<CommentDto> expectedDtos = List.of(dto1, dto2);

        given(commentRepo.getCommentsForBookByBookId(bookId)).willReturn(dbResponse);

        // Настраиваем маппер так, чтобы он возвращал разные объекты в зависимости от входа
        given(mapper.toDto(c1)).willReturn(dto1);
        given(mapper.toDto(c2)).willReturn(dto2);

        // Act
        List<CommentDto> result = commentService.getAllCommentForBook(bookId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Nice!", result.get(0).getText());
        assertEquals("Bad...", result.get(1).getText());

        verify(commentRepo).getCommentsForBookByBookId(bookId);
        verify(mapper).toDto(c1);
        verify(mapper).toDto(c2);
    }

    @Test
    void getAllCommentForBook_NoComments_ReturnsEmptyList() {
        // Arrange
        long bookId = 99L;
        given(commentRepo.getCommentsForBookByBookId(bookId))
                .willReturn(Collections.emptyList());

        // Возвращаем immutable пустой список
        // Act
        List<CommentDto> result = commentService.getAllCommentForBook(bookId);

        // Assert
        assertNotNull(result);

        // Объект списка должен существовать
        assertTrue(result.isEmpty());

        // Но он должен быть пустым
        assertEquals(0, result.size());

        verify(commentRepo).getCommentsForBookByBookId(bookId);
        verify(mapper, never()).toDto(any()); // Маппер не должен вызываться ни разу
    }

    @Test
    void createComment_ValidInput_ReturnsSavedDto() {

        // Arrange (Специфичные настройки поведения под этот тест)
        given(bookRepo.findById(bookId)).willReturn(Optional.of(bookEntity));
        given(commentRepo.save(any(Comment.class))).willReturn(savedComment);
        given(mapper.toDto(savedComment)).willReturn(expectedOutput);

        // Act
        CommentDto result = commentService.createComment(requestDto, bookId);

        // Assert
        assertEquals(100L, result.getId());
        assertEquals(commentText, result.getText());

        verify(bookRepo).findById(bookId);
        verify(commentRepo).save(any(Comment.class));
        verify(mapper).toDto(savedComment); }

    @Test
    void createComment_WhenBookDoesNotExist_ShouldThrowEntityNotFoundException() {
        // Given
        long nonExistentBookId = 999L;
        when(bookRepo.findById(nonExistentBookId)).thenReturn(Optional.empty());

        // When & Then
        // Проверяем, что метод выбрасывает правильное стандартное JPA исключение
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            commentService.createComment(requestDto, nonExistentBookId);
        });

        // Проверяем корректность сообщения об ошибке
        assertEquals("Book not found with id: " + nonExistentBookId, exception.getMessage());

        // Верифицируем: поиск книги вызвался, а сохранение и маппинг проигнорированы
        verify(bookRepo, times(1)).findById(nonExistentBookId);
        verify(commentRepo, never()).save(any(Comment.class));
        verifyNoInteractions(mapper);
    }

    @Test
    void createComment_AssignsBookToComment_BeforeSaving() {
        // Arrange
        given(bookRepo.findById(bookId)).willReturn(Optional.of(bookEntity));
        ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);

        given(commentRepo.save(captor.capture())).willReturn(savedComment);

        // Act
        commentService.createComment(requestDto, bookId);

        // Assert
        Comment capturedValue = captor.getValue();

        assertNotNull(capturedValue.getBook(), "Связь 'книга-комментарий' должна быть установлена");
        assertEquals(bookId, capturedValue.getBook().getId(), "ID книги в комментарии должен совпадать");
    }

    @Test
    void updateComment_ValidInput_ReturnsUpdatedDto() {
        // Arrange
        long commentId = 42L;
        String newText = "Updated text";
        CommentRequest request = new CommentRequest(newText);

        // Заглушка из БД (Старая версия)
        Comment existingComment = new Comment("Old text");
        existingComment.setId(commentId);

        CommentDto expectedOutput = new CommentDto(42L, "Updated text", 2L);

        given(commentRepo.findById(commentId)).willReturn(Optional.of(existingComment));
        given(mapper.toDto(any(Comment.class))).willReturn(expectedOutput);

        // Act
        CommentDto result = commentService.updateComment(request, commentId);

        // Assert результат вызова сервиса
        assertEquals(newText, result.getText());

        // Проверка состояния самого объекта (State-based testing)
        // Важно проверить, что поле изменилось ДО передачи в mapper
        assertEquals(newText, existingComment.getText());

        verify(commentRepo).findById(commentId);
        verify(mapper).toDto(existingComment); // Передаем ссылку на тот же объект
    }

    @Test
    void updateComment_NotFound_ShouldThrowException() {
        // Arrange
        long missingId = 999L;
        CommentRequest request = new CommentRequest("Anything");

        given(commentRepo.findById(missingId)).willReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> {
            commentService.updateComment(request, missingId);
        });

        assertTrue(ex.getMessage().contains("Comment not found"));

        // Критическая проверка: если findById вернул пусто, то setText вызван быть не мог
        verify(commentRepo).findById(missingId);
        verify(mapper, never()).toDto(any()); // До маппера дело не дошло
    }

    @Test
    void updateComment_VerifiesEntityStateBeforeMapping() {
        // Arrange
        long id = 1L;
        String inputText = "New content";
        CommentRequest request = new CommentRequest(inputText);

        Comment dbComment = new Comment("Initial");
        dbComment.setId(id);

        given(commentRepo.findById(id)).willReturn(Optional.of(dbComment));

        ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);
        given(mapper.toDto(captor.capture())).willReturn(new CommentDto(1L, inputText, 2L));

        // Act
        commentService.updateComment(request, id);

        // Assert
        Comment capturedValue = captor.getValue();

        // Это главная проверка этого теста:
        // Мы смотрим внутрь объекта, который полетел в toDto()
        assertEquals(inputText, capturedValue.getText(), "Текст должен быть обновлен до вызова маппера");
        assertEquals(id, capturedValue.getId());

        verify(commentRepo).findById(id);
    }

    @Test
    void deleteComment_CommentExists_RemovesFromBookCollection() {
        // Arrange
        long commentId = 42L;
        Book parentBook = new Book();
        parentBook.setId(1L);

        Comment targetComment = new Comment("To be deleted");
        targetComment.setId(commentId);
        targetComment.setBook(parentBook); // Устанавливаем владельца

        // Инициализируем коллекцию и добавляем объект
        Set<Comment> comments = new HashSet<>();
        comments.add(targetComment);
        parentBook.setComments(comments);
        given(commentRepo.findById(commentId)).willReturn(Optional.of(targetComment));

        // Act
        commentService.deleteComment(commentId);

        // Assert: Проверяем изменение состояния IN-MEMORY объектов
        assertTrue(parentBook.getComments().isEmpty(), "Коллекция комментариев книги должна стать пустой");
        assertNull(targetComment.getBook(), "Ссылка на книгу внутри комментария должна быть очищена");
        // Примечание: вторая проверка пройдет только если ваш remove логика включает .setBook(null)
    }

    @Test
    void deleteComment_NotFound_ShouldThrowException() {
        // Arrange
        long missingId = 999L;
        given(commentRepo.findById(missingId)).willReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> {
            commentService.deleteComment(missingId);
        });

        assertEquals("Comment not found with id: 999", ex.getMessage());

        verify(commentRepo).findById(missingId);
        // Проверяем, что мы даже не пытались лезть в методы Book'а
        verifyNoInteractions(bookRepo);
    }
}
