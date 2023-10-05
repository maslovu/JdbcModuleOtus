package com.maslov.booksmaslov.dao;

import com.maslov.booksmaslov.domain.Author;
import com.maslov.booksmaslov.domain.Book;
import com.maslov.booksmaslov.domain.Comment;
import com.maslov.booksmaslov.domain.Genre;
import com.maslov.booksmaslov.domain.YearOfPublish;
import com.maslov.booksmaslov.model.BookModel;
import com.maslov.booksmaslov.repository.BookRepo;
import lombok.val;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
//@Import({BookDao.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BookDaoTest {
    private static final long ID = 3L;
    private static final String JAVA = "java";
    private static final String STUDYING = "study";
    private static final String TEST = "Test";
    private static final String AUTHOR = "author";
    private static final int INDEX_OF_BOOK = 0;

    private static final int EXPECTED_COUNT = 13;
    public static final long ID_FOR_DELETE = 9L;
    @Autowired
    BookRepo bookRepo;

    @Autowired
    TestEntityManager em;

    @Test
    void getAllBook() {
        SessionFactory sessionFactory = em.getEntityManager().getEntityManagerFactory().unwrap(SessionFactory.class);
        sessionFactory.getStatistics().setStatisticsEnabled(true);

        List<Book> list = bookRepo.findAll();

        assertThat(list.size()).isNotZero();
        assertThat(sessionFactory.getStatistics().getPrepareStatementCount()).isEqualTo(EXPECTED_COUNT);
    }

    @Test
    void getBookById() {
        Book book = bookRepo.findById(ID).orElseThrow();

        String name = book.getGenre().getName();

        List<Author> authors = book.getAuthor();
        List<String> authorsName = new ArrayList<>();
        for (Author a : authors) {
            authorsName.add(a.getName());
        }

        BookModel model = BookModel.builder()
                .name(book.getName())
                .authors(String.valueOf(authorsName))
                .genre(book.getGenre().getName())
                .year(book.getYear().getDateOfPublish())
                .build();

        System.out.println(model);
        assertThat(book.getName()).isEqualTo(JAVA);
        assertThat(model.getGenre()).isEqualTo(STUDYING);
    }

    @Test
    void getBooksByName() {
        List<Book> books = bookRepo.getBooksByName(JAVA);

        assertThat(books.get(0).getId()).isEqualTo(ID);
    }

    @Test
    void createBook() {

        val author = new Author(0, "Lafore");
        val authors = Collections.singletonList(author);
        val year = new YearOfPublish(0, "2021");
        val genre = new Genre(0, "labuda");
        val comment = new Comment(0, "Third");
        val comment2 = new Comment(0, "Five");
        var comments = List.of(comment, comment2);

        var book = new Book(0, TEST, genre, year, authors, comments);

        Book createdBook = bookRepo.save(book);

        assertThat(createdBook.getName()).isEqualTo(TEST);
    }

    @Test
    void updateBook() {
        List<Author> authors = new ArrayList<>();
        authors.add(new Author(0, AUTHOR));
        Genre genre = new Genre(0, "test");
        YearOfPublish year = new YearOfPublish(0, "2015");
        List<Comment> comments = new ArrayList<>();
        comments.add(new Comment());
        Book book = Book.builder()
                .name(TEST)
                .genre(genre)
                .year(year)
                .author(authors)
                .listOfComments(comments)
                .build();
        Book bookFromDB = bookRepo.findById(5L).orElseThrow();
        BeanUtils.copyProperties(book, bookFromDB, "id");

        Book updatedBook = bookRepo.save(bookFromDB);

        assertThat(updatedBook.getName()).isEqualTo(TEST);
    }

    @Test
    void deleteBook() {
        List<Book> booksBefore = bookRepo.findAll();
        bookRepo.deleteById(ID_FOR_DELETE);
        List<Book> booksAfter = bookRepo.findAll();

        assertThat(booksAfter).hasSize(booksBefore.size() - 1);
    }
}
