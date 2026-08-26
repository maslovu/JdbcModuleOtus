package com.maslov.booksmaslov.service.impl;

import com.maslov.booksmaslov.domain.Comment;
import com.maslov.booksmaslov.mapper.CommentMapper;
import com.maslov.booksmaslov.model.CommentDto;
import com.maslov.booksmaslov.model.CommentRequest;
import com.maslov.booksmaslov.repository.BookRepo;
import com.maslov.booksmaslov.repository.CommentRepo;
import com.maslov.booksmaslov.service.CommentService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final BookRepo bookRepo;
    private final CommentRepo commentRepo;
    private final CommentMapper commentMapper;

    public CommentServiceImpl(BookRepo bookRepo, CommentRepo commentRepo, CommentMapper mapper) {
        this.bookRepo = bookRepo;
        this.commentRepo = commentRepo;
        this.commentMapper = mapper;
    }

    @Override
    public Set<CommentDto> getAllCommentForBook(long bookId) {
        var book = bookRepo.getBookById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + bookId));
        return book.getComments().stream()
                .map(commentMapper::toDto)
                .collect(Collectors.toSet());
    }

    @Transactional
    @Override
    public CommentDto createComment(CommentRequest comment, long bookId) {
        var newComment = new Comment(comment.text());
        var book = bookRepo.getBookById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + bookId));
        book.addComment(newComment);
        var createdComment = commentRepo.createComment(newComment);
        return commentMapper.toDto(createdComment);
    }

    @Transactional
    @Override
    public CommentDto updateComment(CommentRequest newComment, long commentId) {
        // Объект переходит в состояние Managed (управляется Hibernate)
        Comment commentFromDb = commentRepo.getCommentById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with id: " + commentId));
        commentFromDb.setText(newComment.text());
        // Вызывать явные методы сохранения типа em.merge() необязательно.
        // Так как метод помечен @Transactional, Hibernate в конце транзакции
        // сам заметит изменение текста и сгенерирует оптимальный SQL UPDATE.

        // Принудительный flush в репозитории здесь не нужен, так как мы не генерируем новый ID,
        // он уже есть в объекте commentFromDb.
        // commentRepo.updateComment(commentFromDb);
        return commentMapper.toDto(commentFromDb);
    }

    @Transactional
    @Override
    public void deleteComment(long commentId) {
        Comment comment = commentRepo.getCommentById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with id: " + commentId));
        if (comment.getBook() != null) {
            comment.getBook().getComments().remove(comment);
        }
        commentRepo.deleteComment(comment);
    }
}
