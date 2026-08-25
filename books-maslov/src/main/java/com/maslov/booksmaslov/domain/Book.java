package com.maslov.booksmaslov.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Table(name = "books")
@Entity
@NamedEntityGraph(name = "author-entity-graph", attributeNodes = {@NamedAttributeNode("authors")})
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name", unique = true, nullable = false)
    private String title;

    @ManyToOne(targetEntity = Genre.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "genre_id")
    private Genre genre;

    @ManyToOne(targetEntity = YearOfPublish.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "year_id")
    private YearOfPublish year;

    @ManyToMany(targetEntity = Author.class, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(name = "books_authors", joinColumns = {@JoinColumn(name = "book_id")},
            inverseJoinColumns = {@JoinColumn(name = "author_id")})
    private Set<Author> authors = new HashSet<>();

    @Getter
    @OneToMany(mappedBy = "book", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    //указываем, если коммент не должен знать о книге @JoinColumn(name = "book_id")
    private Set<Comment> comments = new HashSet<>();

    public void addComment(Comment child) {
        this.comments.add(child);
        child.setBook(this); // Важно синхронизировать обратную связь
    }

    public void removeComment(Comment comment) {
        this.comments.remove(comment);
        comment.setBook(null);
    }

    public void addAuthors(Author author) {
        this.authors.add(author);
        author.getBooks().add(this); // Синхронизируем память Java
    }

    public void removeAuthors(Author author) {
        this.authors.remove(author);
        author.getBooks().remove(this); // Синхронизируем память Java
    }

    @Override
    public String toString() {
        return "Book{" +
                "bookId=" + id +
                ", name='" + title + '\'' +
                ", genre=" + genre.getName() +
                ", year=" + year.getYear() +
                ", authors=" + authors +
                ", comments=" + comments +
                '}';
    }
}
