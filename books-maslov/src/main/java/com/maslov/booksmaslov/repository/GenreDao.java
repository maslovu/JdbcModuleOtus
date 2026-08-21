package com.maslov.booksmaslov.repository;

import com.maslov.booksmaslov.domain.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreDao {
    List<Genre> getAllGenres();

    List<Genre> getGenreByName(String name);

    Optional<Genre> getGenreById(long id);

    Genre createGenre(Genre genre);

}
