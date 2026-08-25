package com.maslov.booksmaslov.repository.impl;

import com.maslov.booksmaslov.domain.Author;
import com.maslov.booksmaslov.repository.AuthorRepo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.maslov.booksmaslov.sql.SQLConstants.GET_ALL_AUTHORS;
import static com.maslov.booksmaslov.sql.SQLConstants.GET_AUTHOR_BY_NAME;
import static java.util.Optional.ofNullable;

@Component
@Slf4j
public class AuthorRepoImpl implements AuthorRepo {

    @PersistenceContext
    private final EntityManager em;

    public AuthorRepoImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public Set<Author> getAllAuthors() {
        var query = em.createQuery(GET_ALL_AUTHORS, Author.class);
        return query.getResultStream().collect(Collectors.toSet());
    }

    @Override
    public Optional<Author> getByName(String name) {
        Author author = null;
        TypedQuery<Author> query = em.createQuery(GET_AUTHOR_BY_NAME, Author.class);
        query.setParameter("author_name", name);
        try {
            author = query.getSingleResult();
        } catch (NoResultException ex) {
            log.error("author not in db, new author will be created in db");
        }
        return ofNullable(author);
    }

    @Override
    public Author getAuthorById(long id) {
        return em.find(Author.class, id);
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

//    private Author checkResult(TypedQuery<Author> query, String name) {
//        try {
//            return query.getSingleResult();
//        } catch (NoResultException e) {
//            log.warn("Has not author with name: {}", name);
//            throw new MaslovBookException(String.format("Has not author with name %s", name));
//        }
//    }
}
