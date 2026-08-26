package com.maslov.booksmaslov.service;

import com.maslov.booksmaslov.domain.Comment;
import com.maslov.booksmaslov.model.CommentDto;
import com.maslov.booksmaslov.model.CommentRequest;

import java.util.Set;

public interface CommentService {

    Set<CommentDto> getAllCommentForBook(long idForBook);

    CommentDto createComment(CommentRequest comment, long idForBook);

    CommentDto updateComment(CommentRequest newComment, long commentId);

    void deleteComment(long idForBook);
}
