package com.maslov.booksmaslov.repository.impl;

import com.maslov.booksmaslov.domain.Author;
import com.maslov.booksmaslov.exception.MaslovBookException;
import com.maslov.booksmaslov.repository.AuthorDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

import static com.maslov.booksmaslov.sql.SQLConstants.GET_ALL_AUTHORS;
import static com.maslov.booksmaslov.sql.SQLConstants.GET_AUTHOR_BY_NAME;

@Component
@Slf4j
public class AuthorDaoImpl implements AuthorDao {

    @PersistenceContext
    private final EntityManager em;

    public AuthorDaoImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public List<Author> getAllAuthors() {
        var query = em.createQuery(GET_ALL_AUTHORS, Author.class);
        return query.getResultList();
    }

    @Override
    public List<Author> getByName(String name) {
        TypedQuery<Author> query = em.createQuery(GET_AUTHOR_BY_NAME, Author.class);
        query.setParameter("author_name", name);
        return checkResult(query, name);
    }

    @Override
    public Optional<Author> getAuthorById(long id) {
        return Optional.ofNullable(em.find(Author.class, id));
    }

    @Override
    public Author createAuthor(Author author) {
        log.info("Created new Author");
        if (author.getId() == 0) {
            em.persist(author);
            return author;
        }
        return em.merge(author);
    }

    private List<Author> checkResult(TypedQuery<Author> query, String name) {
        try {
            return query.getResultList();
        } catch (NoResultException e) {
            log.warn("Has not author with name: {}", name);
            throw new MaslovBookException(String.format("Has not author with name %s", name));
        }
    }
}
