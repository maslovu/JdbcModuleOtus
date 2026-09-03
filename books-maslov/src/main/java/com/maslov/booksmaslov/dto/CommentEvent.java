package com.maslov.booksmaslov.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CommentEvent {
    @JsonProperty("book_id")
    private long bookId;
    @JsonProperty("comment")
    private String comment;
}
