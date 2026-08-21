package com.maslov.booksmaslov.service.impl;

import com.maslov.booksmaslov.domain.Comment;
import com.maslov.booksmaslov.repository.BookDao;
import com.maslov.booksmaslov.repository.CommentDao;
import com.maslov.booksmaslov.service.CommentService;
import com.maslov.booksmaslov.service.ScannerHelper;
import com.maslov.booksmaslov.service.ServiceHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final ScannerHelper helper;
    private final BookDao bookDao;
    private final CommentDao commentDao;

    private final ServiceHelper serviceHelper;

    public CommentServiceImpl(ScannerHelper helper, BookDao bookDao, CommentDao commentDao, ServiceHelper serviceHelper) {
        this.helper = helper;
        this.bookDao = bookDao;
        this.commentDao = commentDao;
        this.serviceHelper = serviceHelper;
    }

    @Transactional
    @Override
    public Set<Comment> createComment() {
        System.out.println("Enter your comment");
        String comm = helper.getFromUser();
        Comment addedComment = commentDao.createComment(comm);
        Set<Comment> commentList = new HashSet<>();
        commentList.add(addedComment);
        return commentList;
    }

    @Transactional
    @Override
    public Set<Comment> updateComment() {
        int idForBook = serviceHelper.getIdForBook();
        int idComment = serviceHelper.getCommentId(idForBook);
        System.out.println("Enter correct comment");
        String newComment = helper.getFromUser();
        commentDao.updateComment(new Comment(idComment, newComment));
        return bookDao.getBookById(idForBook).orElseThrow().getListOfComments();
    }

    @Override
    public Set<Comment> deleteComment() {
        int idForBook = serviceHelper.getIdForBook();
        int idForComment = serviceHelper.getCommentId(idForBook);
        Comment comment = commentDao.getCommentById(idForComment);
        commentDao.deleteComment(comment);
        return bookDao.getBookById(idForBook).orElseThrow().getListOfComments();
    }
}
