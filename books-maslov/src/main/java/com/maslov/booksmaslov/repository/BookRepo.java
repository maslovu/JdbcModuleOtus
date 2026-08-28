package com.maslov.booksmaslov.repository;

import com.maslov.booksmaslov.domain.Book;
import com.maslov.booksmaslov.dto.BookDto;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BookRepo extends JpaRepository<Book, Long> {

    @Query("SELECT NEW com.maslov.booksmaslov.dto.BookDto(" +
            "b.id AS id, " +
            "b.title AS title, " +
            "a.name AS authorName," +
            "g.id AS genreId, " +
            "y.id AS yearId ) " +
            "FROM Book b " +
            "JOIN b.authors a " +
            "LEFT JOIN b.genre g " +
            "LEFT JOIN b.year y ")
    List<BookDto> findAllBooks();

    //Ищет книгу и гарантированно подтягивает авторов/жанр одним запросом
    @EntityGraph(attributePaths = {"genre", "year", "authors"})
    Optional<Book> findById(Long id);
}
