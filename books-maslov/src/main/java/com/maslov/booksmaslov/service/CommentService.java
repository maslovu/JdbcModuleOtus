package com.maslov.booksmaslov.service;

import com.maslov.booksmaslov.domain.Comment;

import java.util.Set;

public interface CommentService {

    Comment createComment(String comment, int idForBook);

    void updateComment(int idOfComment, String newComment);

    void deleteComment(int idForBook);
}
