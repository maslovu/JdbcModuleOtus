package com.maslov.booksmaslov.repository.impl;

import com.maslov.booksmaslov.domain.Genre;
import com.maslov.booksmaslov.repository.GenreDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({GenreDaoImpl.class})
class GenreDaoImplTest {

    private static final long ID = 1;
    private static final String NAME = "study";
    private static final String PYTHON = "python";

    @Autowired
    private GenreDao dao;

    @Test
    void getAllNames() {
        List<Genre> list = dao.getAllGenres();

        assertThat(list.size()).isNotZero();
    }

    @Test
    void getByName() {
        Genre genre = dao.getGenreByName(NAME).get(0);

        assertThat(genre.getName()).isEqualTo(NAME);
    }

    @Test
    void getGenreById() {
        Genre genre = dao.getGenreById(ID).get();

        assertThat(genre.getName()).isEqualTo(NAME);
    }

    @Test
    void getGenreId() {
        Long id = dao.getGenreByName(NAME).get(0).getId();

        assertThat(id).isEqualTo(ID);
    }

    @Test
    void createGenre() {
        Genre genre = new Genre(0, PYTHON);

        Genre resGenre = dao.createGenre(genre);

        assertThat(resGenre.getName()).isEqualTo(PYTHON);
    }
}