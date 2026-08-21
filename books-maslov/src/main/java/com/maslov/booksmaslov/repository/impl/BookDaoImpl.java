package com.maslov.booksmaslov.repository.impl;

import com.maslov.booksmaslov.domain.Book;
import com.maslov.booksmaslov.exception.MaslovBookException;
import com.maslov.booksmaslov.repository.BookDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

import static com.maslov.booksmaslov.sql.SQLConstants.GET_ALL_BOOKS;
import static com.maslov.booksmaslov.sql.SQLConstants.SELECT_BOOK_BY_NAME;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookDaoImpl implements BookDao {
    @PersistenceContext
    private final EntityManager em;

    @Override
    public List<Book> getAllBook() {
        EntityGraph<?> entityGraph = em.getEntityGraph("author-entity-graph");
        var allBook = em.createQuery(GET_ALL_BOOKS, Book.class);

        allBook.setHint("javax.persistence.fetchgraph", entityGraph);

        return allBook.getResultList();
    }

    @Override
    public Optional<Book> getBookById(long id) {
        return Optional.ofNullable(em.find(Book.class, id));
    }

    @Override
    public List<Book> getBooksByName(String name) {
        TypedQuery<Book> query = em.createQuery(SELECT_BOOK_BY_NAME, Book.class);
        query.setParameter("name", name);
        return checkResult(query, name);
    }

    @Override
    public Book createBook(Book book) {
        em.persist(book);
        return book;
    }

    @Override
    public Book updateBook(Book book) {
        return em.merge(book);
    }

    @Override
    public void deleteBook(Book book) {
        em.remove(book);
    }

    private List<Book> checkResult(TypedQuery<Book> query, String name) {
        try {
            return query.getResultList();
        } catch (NoResultException e) {
            log.warn("Has not author with name: {}", name);
            throw new MaslovBookException(String.format("Has not author with name %s", name));
        }
    }
}
