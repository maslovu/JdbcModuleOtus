package com.maslov.booksmaslov.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CommentEvent(
        @JsonProperty("book_id") long bookId,
        @JsonProperty("comment") String comment) {}
