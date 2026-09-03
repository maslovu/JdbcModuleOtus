package com.maslov.booksmaslov.service;

import com.maslov.booksmaslov.dto.CommentEvent;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentProcessingService {
    private static final int MAX_ATTEMPTS = 3;

    // Инжектим наш основной сервис или репозиторий напрямую
    private final CommentService commentService;

    public CommentProcessingService(CommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * Этот метод выступает "оберткой". Он ловит конфликты и перезапускает процесс.
     */
    @Retryable(
            value = {OptimisticLockingFailureException.class},
            maxAttemptsExpression = "${retry.max-attempts:3}",
            backoff = @Backoff(delayExpression = "${retry.backoff-delay:1000}"))
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processWithRetry(List<CommentEvent> events) {
        // Вызываем внутреннюю логику без обработки ошибок внутри
        commentService.createCommentFromBatch(events);
    }
}
