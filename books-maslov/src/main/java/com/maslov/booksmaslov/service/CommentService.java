package com.maslov.booksmaslov.service;

import com.maslov.booksmaslov.domain.Comment;

import java.util.Set;

public interface CommentService {

    Set<Comment> createComment();

    Set<Comment> updateComment();

    Set<Comment> deleteComment();
}
