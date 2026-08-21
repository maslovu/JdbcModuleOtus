package com.maslov.booksmaslov.service;

import com.maslov.booksmaslov.domain.Comment;

import java.util.List;

public interface CommentService {

    List<Comment> createComment();

    List<Comment> updateComment();

    List<Comment> deleteComment();
}
