package com.maslov.booksmaslov.service;

import com.maslov.booksmaslov.dto.CommentDto;
import com.maslov.booksmaslov.dto.CommentEvent;
import com.maslov.booksmaslov.dto.CommentRequest;

import java.util.List;

public interface CommentService {

    List<CommentDto> getAllCommentForBook(long bookId);

    CommentDto createComment(CommentRequest comment, long bookId);

    void createCommentFromBatch(List<CommentEvent> comments);

    CommentDto updateComment(CommentRequest newComment, long commentId, long bookId);

    void deleteComment(long commentId, long bookId);
}
