package com.maslov.booksmaslov.service;

import com.maslov.booksmaslov.domain.Comment;

public interface CommentService {

    Comment createComment(String comment, int idForBook);

    void updateComment(int idOfComment, String newComment);

    void deleteComment(int idForBook);
}
