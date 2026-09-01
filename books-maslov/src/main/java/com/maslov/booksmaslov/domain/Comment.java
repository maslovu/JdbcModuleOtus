package com.maslov.booksmaslov.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private long id;

    @Column(name = "comment_book")
    private String text;

    // Конструкторы, геттеры, сеттеры
    @Setter
    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false) // Имя колонки FK в таблице book
//    @JsonBackReference // Это поле НЕ ПОЙДЕТ в JSON комментария. Цикл разорван!
    private Book book;

    public Comment(String comment) {
        this.text = comment;
    }

    @Override
    public String toString() {
        return id + ": " + text;
    }
}
