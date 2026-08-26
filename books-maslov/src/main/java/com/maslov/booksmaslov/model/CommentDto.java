package com.maslov.booksmaslov.model;

public class CommentDto {
    private Long id;
    private String text;
    private Long bookId; // Вместо объекта Book — только его идентификатор

    // Конструкторы
    public CommentDto() {}

    public CommentDto(Long id, String text, Long bookId) {
        this.id = id;
        this.text = text;
        this.bookId = bookId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    @Override
    public String toString() {
        return "CommentDto{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", bookId=" + bookId +
                '}';
    }
}