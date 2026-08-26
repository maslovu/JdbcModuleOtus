package com.maslov.booksmaslov.repository.impl;

import com.maslov.booksmaslov.domain.Book;
import com.maslov.booksmaslov.domain.Comment;
import com.maslov.booksmaslov.exception.MaslovBookException;
import com.maslov.booksmaslov.repository.CommentRepo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static java.util.Objects.isNull;

@Slf4j
@Component
public class CommentRepoImpl implements CommentRepo {

    @PersistenceContext
    private final EntityManager em;

    public CommentRepoImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public Optional<Comment> getCommentById(long id) {
        if (isNull(em.find(Comment.class, id))) {
            throw new MaslovBookException("No comment for this ID");
        }
        return Optional.ofNullable(em.find(Comment.class, id));
    }

    @Override
    public Comment createComment(Comment comm) {
        log.info("Created new Comment");
        em.persist(comm);
        em.flush();
        return comm;
    }

    @Override
    public void updateComment(Comment comment) {
        em.merge(comment);
    }

    @Override
    public void deleteComment(Comment comment) {
        // Проверяем: если объект вдруг потерял связь с сессией (detached),
        // мы сначала привязываем его обратно через merge(), а затем удаляем
        Comment managedComment = em.contains(comment) ? comment : em.merge(comment);
        em.remove(managedComment);
    }

    private Book checkResult(TypedQuery<Book> query, Long id) {
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            log.warn("Has not author with name: {}", id);
            throw new MaslovBookException(String.format("Has not author with name %s", id));
        }
    }
}
