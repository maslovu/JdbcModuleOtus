package com.maslov.booksmaslov.service.impl;

import com.maslov.booksmaslov.domain.Book;
import com.maslov.booksmaslov.domain.Comment;
import com.maslov.booksmaslov.dto.CommentDto;
import com.maslov.booksmaslov.dto.CommentRequest;
import com.maslov.booksmaslov.exception.ResourceNotFoundException;
import com.maslov.booksmaslov.mapper.CommentMapper;
import com.maslov.booksmaslov.repository.BookRepo;
import com.maslov.booksmaslov.repository.CommentRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

    private long bookId;

    private CommentRequest commentRequest;
    private Book bookEntity;
    private Comment savedComment;
    private CommentDto expectedOutput;

    private final String COMMENT_TEXT = "Great read!";

    @BeforeEach
    void setUp() {
        bookId = 42L;

        commentRequest = new CommentRequest(COMMENT_TEXT);

        bookEntity = new Book();
        bookEntity.setId(bookId);
        savedComment = new Comment(COMMENT_TEXT);
        savedComment.setId(100L);
        savedComment.setBook(bookEntity);

        expectedOutput = new CommentDto(100L, COMMENT_TEXT, 1L);
    }

    @Test
    void getAllCommentForBook_WhenCommentsExist_ReturnsListOfDtos() {
        // Given
        long bookId = 42L;

        Comment c1 = new Comment("Nice!");
        Comment c2 = new Comment("Bad...");

        List<Comment> dbResponse = List.of(c1, c2);

        CommentDto dto1 = new CommentDto(1L, "Nice!", 3L);
        CommentDto dto2 = new CommentDto(2L, "Bad...", 3L);

        given(commentRepo.getCommentsForBookByBookId(bookId)).willReturn(dbResponse);

        given(mapper.toDto(c1)).willReturn(dto1);
        given(mapper.toDto(c2)).willReturn(dto2);

        // When
        List<CommentDto> result = commentService.getAllCommentForBook(bookId);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Nice!", result.get(0).text());
        assertEquals("Bad...", result.get(1).text());

        verify(commentRepo).getCommentsForBookByBookId(bookId);
        verify(mapper).toDto(c1);
        verify(mapper).toDto(c2);
    }

    @Test
    void getAllCommentForBook_NoComments_ReturnsEmptyList() {
        // Given
        long bookId = 99L;
        given(commentRepo.getCommentsForBookByBookId(bookId))
                .willReturn(Collections.emptyList());

        // When
        List<CommentDto> result = commentService.getAllCommentForBook(bookId);

        // Then
        assertNotNull(result);

        // Объект списка должен существовать
        assertTrue(result.isEmpty());

        verify(commentRepo).getCommentsForBookByBookId(bookId);
        verify(mapper, never()).toDto(any()); // Маппер не должен вызываться ни разу
    }

    @Test
    void createComment_ValidInput_ReturnsSavedDto() {

        // Given
        given(bookRepo.findById(bookId)).willReturn(Optional.of(bookEntity));
        given(commentRepo.save(any(Comment.class))).willReturn(savedComment);
        given(mapper.toDto(savedComment)).willReturn(expectedOutput);

        // When
        CommentDto result = commentService.createComment(commentRequest, bookId);

        // Then
        assertEquals(100L, result.id());
        assertEquals(COMMENT_TEXT, result.text());

        verify(bookRepo).findById(bookId);
        verify(commentRepo).save(any(Comment.class));
        verify(mapper).toDto(savedComment); }

    @Test
    void createComment_WhenBookDoesNotExist_ShouldThrowResourceNotFoundException() {
        // Given
        long nonExistentBookId = 999L;
        when(bookRepo.findById(nonExistentBookId)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            commentService.createComment(commentRequest, nonExistentBookId);
        });

        assertEquals("Book/comment not found with id : " + nonExistentBookId, exception.getMessage());

        // Верифицируем: поиск книги вызвался, а сохранение и маппинг проигнорированы
        verify(bookRepo, times(1)).findById(nonExistentBookId);
        verify(commentRepo, never()).save(any(Comment.class));
        verifyNoInteractions(mapper);
    }

    @Test
    void createComment_AssignsBookToComment_BeforeSaving() {
        // Given
        given(bookRepo.findById(bookId)).willReturn(Optional.of(bookEntity));
        ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);

        given(commentRepo.save(captor.capture())).willReturn(savedComment);

        // When
        commentService.createComment(commentRequest, bookId);

        // Then
        Comment capturedValue = captor.getValue();

        assertNotNull(capturedValue.getBook(), "Связь 'книга-комментарий' должна быть установлена");
        assertEquals(bookId, capturedValue.getBook().getId(), "ID книги в комментарии должен совпадать");
    }

    @Test
    void updateComment_ValidInput_ReturnsUpdatedDto() {
        // Given
        long commentId = 42L;
        String newText = "Updated text";
        CommentRequest request = new CommentRequest(newText);

        Comment existingComment = new Comment("Old text");
        existingComment.setId(commentId);

        CommentDto expectedOutput = new CommentDto(42L, newText, 2L);

        given(commentRepo.findById(commentId)).willReturn(Optional.of(existingComment));
        given(mapper.toDto(any(Comment.class))).willReturn(expectedOutput);

        // When
        CommentDto result = commentService.updateComment(request, commentId, bookId);

        // Then
        assertEquals(newText, result.text());

        // Проверка состояния самого объекта (State-based testing)
        // Важно проверить, что поле изменилось ДО передачи в mapper
        assertEquals(newText, existingComment.getText());

        verify(commentRepo).findById(commentId);
        verify(mapper).toDto(existingComment);
    }

    @Test
    void updateComment_NotFound_ShouldThrowException() {
        // Given
        long missingId = 999L;

        CommentRequest request = new CommentRequest(COMMENT_TEXT);
        given(commentRepo.findById(missingId)).willReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> {
            commentService.updateComment(request, missingId, bookId);
        });

        assertTrue(ex.getMessage().contains("Comment not found"));

        // Критическая проверка: если findById вернул пусто, то setText вызван быть не мог
        verify(commentRepo).findById(missingId);
        verify(mapper, never()).toDto(any());
    }

    @Test
    void updateComment_VerifiesEntityStateBeforeMapping() {
        // Given
        long id = 1L;
        String inputText = "New content";
        CommentRequest request = new CommentRequest(inputText);
//        CommentEvent event = new CommentEvent(inputText);
//        event.setComment(inputText);

        Comment dbComment = new Comment("Initial");
        dbComment.setId(id);

        given(commentRepo.findById(id)).willReturn(Optional.of(dbComment));

        ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);
        given(mapper.toDto(captor.capture())).willReturn(new CommentDto(1L, inputText, 2L));

        // When
        commentService.updateComment(request, id, bookId);

        // Then
        Comment capturedValue = captor.getValue();

        assertEquals(inputText, capturedValue.getText(), "Текст должен быть обновлен до вызова маппера");
        assertEquals(id, capturedValue.getId());

        verify(commentRepo).findById(id);
    }

    @Test
    void deleteComment_CommentExists_RemovesFromBookCollection() {
        // Given
        long commentId = 42L;
        Book parentBook = new Book();
        parentBook.setId(1L);

        Comment targetComment = new Comment("To be deleted");
        targetComment.setId(commentId);
        targetComment.setBook(parentBook);

        Set<Comment> comments = new HashSet<>();
        comments.add(targetComment);
        parentBook.setComments(comments);
        given(commentRepo.findById(commentId)).willReturn(Optional.of(targetComment));

        // When
        commentService.deleteComment(commentId, bookId);

        // Then: Проверяем изменение состояния IN-MEMORY объектов
        assertTrue(parentBook.getComments().isEmpty(), "Коллекция комментариев книги должна стать пустой");
        assertNull(targetComment.getBook(), "Ссылка на книгу внутри комментария должна быть очищена");
        // Примечание: вторая проверка пройдет только если ваш remove логика включает .setBook(null)
    }

    @Test
    void deleteComment_NotFound_ShouldThrowException() {
        // Given
        long missingId = 999L;
        given(commentRepo.findById(missingId)).willReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> {
            commentService.deleteComment(missingId, bookId);
        });

        assertEquals("Comment not found with id : 999", ex.getMessage());

        verify(commentRepo).findById(missingId);
        verifyNoInteractions(bookRepo);
    }
}
