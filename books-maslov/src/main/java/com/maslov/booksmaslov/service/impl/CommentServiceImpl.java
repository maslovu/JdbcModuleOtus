package com.maslov.booksmaslov.service.impl;

import com.maslov.booksmaslov.domain.Comment;
import com.maslov.booksmaslov.repository.BookRepo;
import com.maslov.booksmaslov.repository.CommentRepo;
import com.maslov.booksmaslov.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final BookRepo bookRepo;
    private final CommentRepo commentDao;

    public CommentServiceImpl(BookRepo bookRepo, CommentRepo commentDao) {
        this.bookRepo = bookRepo;
        this.commentDao = commentDao;
    }

    @Transactional
    @Override
    public Comment createComment(String comment, int idForBook) {
        var newComment =  new Comment(comment);
        bookRepo.getBookById(idForBook).addComment(newComment);
        return newComment;
    }

    @Transactional
    @Override
    public void updateComment(int idOfComment, String newComment) {
        Comment comment = commentDao.getCommentById(idOfComment);
        comment.setComment(newComment);
        commentDao.updateComment(comment);
    }

    @Override
    public void deleteComment(int idOfComment) {
        Comment comment = commentDao.getCommentById(idOfComment);
        commentDao.deleteComment(comment);
    }
}
