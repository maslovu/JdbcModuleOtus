package com.maslov.booksmaslov.repository;

import com.maslov.booksmaslov.domain.Comment;

public interface CommentRepo {

    Comment getCommentById(long id);

    Comment createComment(String comment);

    void updateComment(Comment comment);

    void deleteComment(Comment comment);
}
