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

import static java.util.Objects.isNull;

@Slf4j
@Component
public class CommentDaoImpl implements CommentRepo {

    @PersistenceContext
    private final EntityManager em;

    public CommentDaoImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public Comment getCommentById(long id) {
        if (isNull(em.find(Comment.class, id))) {
            throw new MaslovBookException("No comment for this ID");
        }
        return em.find(Comment.class, id);
    }

    @Override
    public Comment createComment(String comment) {
        Comment comm = new Comment(comment);
        log.info("Created new Comment");
        em.persist(comm);
        return comm;
    }

    @Override
    public void updateComment(Comment comment) {
        em.merge(comment);
    }

    @Override
    public void deleteComment(Comment comment) {
        em.remove(em.contains(comment) ? comment : em.merge(comment));
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
