package com.maslov.booksmaslov.domain;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private long commentId;

    @Column(name = "comment")
    private String comment;

    // Конструкторы, геттеры, сеттеры
    @Setter
    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false) // Имя колонки FK в таблице book
    private Book book;

    public Comment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return commentId + ": " + comment;
    }
}
