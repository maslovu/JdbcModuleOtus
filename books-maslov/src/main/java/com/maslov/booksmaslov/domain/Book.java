package com.maslov.booksmaslov.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "books")
@Entity
@NamedEntityGraph(name = "author-entity-graph", attributeNodes = {@NamedAttributeNode("authors")})
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long bookId;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

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

    public void addChild(Comment child) {
        this.comments.add(child);
        child.setBook(this); // Важно синхронизировать обратную связь
    }

    public void addComment(Comment comment) {
        this.comments.add(comment);
        comment.setBook(this);
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
                "bookId=" + bookId +
                ", name='" + name + '\'' +
                ", genre=" + genre.getName() +
                ", year=" + year.getDateOfPublish() +
                ", authors=" + authors +
                ", comments=" + comments +
                '}';
    }
}
