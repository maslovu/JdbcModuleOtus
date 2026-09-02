package com.maslov.booksmaslov.service.impl;

import com.maslov.booksmaslov.domain.Book;
import com.maslov.booksmaslov.domain.Comment;
import com.maslov.booksmaslov.dto.CommentDto;
import com.maslov.booksmaslov.dto.CommentEvent;
import com.maslov.booksmaslov.dto.CommentRequest;
import com.maslov.booksmaslov.mapper.CommentMapper;
import com.maslov.booksmaslov.repository.BookRepo;
import com.maslov.booksmaslov.repository.CommentRepo;
import com.maslov.booksmaslov.service.CommentService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class CommentServiceImpl implements CommentService {
    private static final int FLUSH_THRESHOLD = 10;

    private final BookRepo bookRepo;
    private final CommentRepo commentRepo;
    private final CommentMapper mapper;

    @PersistenceContext
    private EntityManager entityManager;

    public CommentServiceImpl(BookRepo bookRepo, CommentRepo commentRepo, CommentMapper mapper) {
        this.bookRepo = bookRepo;
        this.commentRepo = commentRepo;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getAllCommentForBook(long bookId) {
        return commentRepo.getCommentsForBookByBookId(bookId).stream()
                .map(mapper::toDto).toList();
    }

    @Transactional
    @Override
    public CommentDto createComment(CommentRequest comment, long bookId) {
        var newComment = new Comment(comment.getText());
        var book = bookRepo.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + bookId));
        book.addComment(newComment);
        Comment savedComment = commentRepo.save(newComment);
        return mapper.toDto(savedComment);
    }

    @Override
    public void createCommentFromBatch(List<CommentEvent> events) {


        List<Comment> toSave = new ArrayList<>(events.size());

        Set<Long> bookIds = events.stream().map(CommentEvent::getBookId).collect(Collectors.toSet());
        Map<Long, Book> bookCache = bookRepo.findAllById(bookIds).stream()
                .collect(Collectors.toMap(Book::getId, b -> b));

        for (var event : events) {
            Book book = bookCache.get(event.getBookId());
            if (book == null) {
                throw new NoSuchElementException("Book not found with id: " + event.getBookId());
            }

            Comment comment = new Comment();
            comment.setText(event.getComment());
            comment.setBook(book);
            toSave.add(comment);

            if (toSave.size() >= FLUSH_THRESHOLD) {
                persistAndClear(toSave);
            }
        }

        if (!toSave.isEmpty()) {
            persistAndClear(toSave);
        }
    }

    @Transactional
    @Override
    public CommentDto updateComment(CommentRequest newComment, long commentId) {
        // Объект переходит в состояние Managed (управляется Hibernate)
        Comment commentFromDb = commentRepo.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with id: " + commentId));
        commentFromDb.setText(newComment.getText());
        // Вызывать явные методы сохранения типа em.merge() необязательно.
        // Так как метод помечен @Transactional, Hibernate в конце транзакции
        // сам заметит изменение текста и сгенерирует оптимальный SQL UPDATE.

        // Принудительный flush в репозитории здесь не нужен, так как мы не генерируем новый ID,
        // он уже есть в объекте commentFromDb.
        // commentRepo.updateComment(commentFromDb);
        return mapper.toDto(commentFromDb);
    }

    @Transactional
    @Override
    public void deleteComment(long commentId) {
        Comment comment = commentRepo.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with id: " + commentId));
        if (comment.getBook() != null) {
            comment.getBook().getComments().remove(comment);
            comment.setBook(null);
        }
    }

    private void persistAndClear(List<Comment> batch) {
        commentRepo.saveAllAndFlush(batch);
        // === КРИТИЧНО ДЛЯ ПАМЯТИ ===
        // Освобождаем память Persistence Context (L1 Cache)
        entityManager.clear();
        // Очищаем Java-коллекцию, чтобы GC мог забрать старые объекты
        batch.clear();
    }
}
