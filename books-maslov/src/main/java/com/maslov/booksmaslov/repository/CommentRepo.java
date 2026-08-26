package com.maslov.booksmaslov.repository;

import com.maslov.booksmaslov.domain.Comment;

import java.util.Optional;

public interface CommentRepo {

    Optional<Comment> getCommentById(long id);

    Comment createComment(Comment comment);

    void updateComment(Comment comment);

    void deleteComment(Comment comment);
}
