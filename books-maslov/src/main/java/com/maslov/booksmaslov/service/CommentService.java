package com.maslov.booksmaslov.service;

import com.maslov.booksmaslov.dto.CommentDto;
import com.maslov.booksmaslov.dto.CommentEvent;
import com.maslov.booksmaslov.dto.CommentRequest;

import java.util.List;

public interface CommentService {

    List<CommentDto> getAllCommentForBook(long idForBook);

    CommentDto createComment(CommentRequest comment, long idForBook);

    void createCommentFromBatch(List<CommentEvent> comments);

    CommentDto updateComment(CommentRequest newComment, long commentId);

    void deleteComment(long idForBook);
}
