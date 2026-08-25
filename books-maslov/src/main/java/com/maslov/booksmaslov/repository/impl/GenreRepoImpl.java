package com.maslov.booksmaslov.repository.impl;

import com.maslov.booksmaslov.domain.Genre;
import com.maslov.booksmaslov.repository.GenreRepo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static com.maslov.booksmaslov.sql.SQLConstants.GET_ALL_GENRES;
import static com.maslov.booksmaslov.sql.SQLConstants.GET_GENRE_BY_NAME;
import static java.util.Optional.ofNullable;

@Component
@Slf4j
public class GenreRepoImpl implements GenreRepo {

    @PersistenceContext
    private final EntityManager em;

    public GenreRepoImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public List<Genre> getAllGenres() {
        var query = em.createQuery(GET_ALL_GENRES, Genre.class);
        return query.getResultList();
    }

    @Override
    public Optional<Genre> getGenreById(long id) {
        return ofNullable(em.find(Genre.class, id));
    }

    @Override
    public Optional<Genre> getGenreByName(String name) {
        Genre genre = null;
        var query = em.createQuery(GET_GENRE_BY_NAME, Genre.class);
        query.setParameter("name", name);
        try {
            genre = query.getSingleResult();
        } catch (NoResultException ex) {
            log.error("genre not in db, new genre will be created in db");
        }
        return ofNullable(genre);
    }

    @Override
    public Genre createGenre(Genre genre) {
        log.info("Created new Genre");
        if (genre.getId() == 0) {
            em.persist(genre);
            return genre;
        }
        return em.merge(genre);
    }

//    private Optional<Genre> checkResult(TypedQuery<Genre> query, String name) {
//        try {
//            return Optional.
//        } catch (NoResultException e) {
//            log.warn("Has not author with name: {}", name);
//            throw new MaslovBookException(String.format("Has not genre with name %s", name));
//        }
//    }
}
