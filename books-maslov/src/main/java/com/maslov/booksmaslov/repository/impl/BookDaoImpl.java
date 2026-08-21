package com.maslov.booksmaslov.repository.impl;

import com.maslov.booksmaslov.domain.Book;
import com.maslov.booksmaslov.exception.MaslovBookException;
import com.maslov.booksmaslov.repository.BookDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.util.List;
import java.util.Optional;

import static com.maslov.booksmaslov.sql.SQLConstants.*;

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


    public Book getBookById(long id) {
        //todo
        TypedQuery<Book> query = em.createQuery(SELECT_BOOK_BY_ID, Book.class);
        query.setParameter("id", id);
        return query.getSingleResult();
    }


//    @Override
//    public Optional<Book> findBookWithMetadata(Long id) {
//        return em.find(Book.class, id).orElseThrow(() -> new EntityNotFoundException("Книга не найдена"));;
//    }
//
//    @Override
//    public Optional<Book> findBookWithComments(Long id) {
//        return Optional.ofNullable(em.find(Book.class, id));
//    }

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
            log.warn("Has not book with name: {}", name);
            throw new MaslovBookException(String.format("Has not book with name %s", name));
        }
    }
}
