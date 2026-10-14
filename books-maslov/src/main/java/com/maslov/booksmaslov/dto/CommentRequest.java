package com.maslov.booksmaslov.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CommentRequest(@JsonProperty("text") String text) {}
