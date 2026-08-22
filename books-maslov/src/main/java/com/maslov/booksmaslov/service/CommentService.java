package com.maslov.booksmaslov.service;

import com.maslov.booksmaslov.domain.Comment;

import java.util.Set;

public interface CommentService {

    Comment createComment();

    void updateComment();

    Set<Comment> deleteComment();
}
