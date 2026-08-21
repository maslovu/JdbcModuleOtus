package com.maslov.booksmaslov.domain;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
@NoArgsConstructor
@Entity
@Table(name = "authors")
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "author_name", nullable = false, unique = true)
    private String authorName;

    // Геттеры, сеттеры, equals/hashCode строго по бизнес-ключу (без коллекции books!)
    // mappedBy указывает на поле 'authors' в классе Book (владельце связи)
    @Getter
    @ManyToMany(mappedBy = "authors")
    private Set<Book> books = new HashSet<>();

    public Author(String authorName) {
        this.authorName = authorName;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Author author = (Author) o;
        return Objects.equals(authorName, author.authorName);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(authorName);
    }

    @Override
    public String toString() {
        return authorName;
    }
}
