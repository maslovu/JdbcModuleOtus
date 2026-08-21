package com.maslov.booksmaslov.repository.impl;

import com.maslov.booksmaslov.domain.Genre;
import com.maslov.booksmaslov.exception.MaslovBookException;
import com.maslov.booksmaslov.repository.GenreDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

import static com.maslov.booksmaslov.sql.SQLConstants.GET_ALL_GENRES;
import static com.maslov.booksmaslov.sql.SQLConstants.GET_GENRE_BY_NAME;


@Component
@Slf4j
public class GenreDaoImpl implements GenreDao {

    @PersistenceContext
    private final EntityManager em;

    public GenreDaoImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public List<Genre> getAllGenres() {
        var query = em.createQuery(GET_ALL_GENRES, Genre.class);
        return query.getResultList();
    }

    @Override
    public Optional<Genre> getGenreById(long id) {
        return Optional.ofNullable(em.find(Genre.class, id));
    }

    @Override
    public List<Genre> getGenreByName(String name) {
        var query = em.createQuery(GET_GENRE_BY_NAME, Genre.class);
        query.setParameter("name", name);
        return checkResult(query, name);
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

    private List<Genre> checkResult(TypedQuery<Genre> query, String name) {
        try {
            return query.getResultList();
        } catch (NoResultException e) {
            log.warn("Has not author with name: {}", name);
            throw new MaslovBookException(String.format("Has not genre with name %s", name));
        }
    }
}
