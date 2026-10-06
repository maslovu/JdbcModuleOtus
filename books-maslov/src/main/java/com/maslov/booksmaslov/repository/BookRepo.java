package com.maslov.booksmaslov.repository;

import com.maslov.booksmaslov.domain.Book;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BookRepo extends JpaRepository<Book, Long> {

    @EntityGraph(attributePaths = {"genre", "year", "authors"})
    @Query("SELECT DISTINCT b FROM Book b")
    List<Book> findAllBooks();

    //Ищет книгу и гарантированно подтягивает авторов/жанр одним запросом
    @EntityGraph(attributePaths = {"genre", "year", "authors"})
    Optional<Book> findById(Long id);
}
