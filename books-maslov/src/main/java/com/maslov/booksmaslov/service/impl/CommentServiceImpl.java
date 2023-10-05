package com.maslov.booksmaslov.service.impl;

import com.maslov.booksmaslov.domain.Book;
import com.maslov.booksmaslov.domain.Comment;
import com.maslov.booksmaslov.repository.BookRepo;
import com.maslov.booksmaslov.repository.CommentRepo;
import com.maslov.booksmaslov.service.CommentService;
import com.maslov.booksmaslov.service.ScannerHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final ScannerHelper helper;
    private final BookRepo bookRepo;
    private final CommentRepo commentRepo;

    public CommentServiceImpl(ScannerHelper helper, BookRepo bookRepo, CommentRepo commentRepo) {
        this.helper = helper;
        this.bookRepo = bookRepo;
        this.commentRepo = commentRepo;
    }

    @Override
    @Transactional
    public List<Comment> createComment() {
        long idForBook = getIdForBook();
        helper.getEmptyString();
        System.out.println("Enter your comment");
        Comment comm = new Comment(0, helper.getFromUser());
        Comment addedComment = commentRepo.save(comm);
        Book bookFromDB = bookRepo.findById(idForBook).orElseThrow();
        List<Comment> commentList = bookFromDB.getListOfComments();
        commentList.add(addedComment);
        Book book = Book.builder()
                .name(bookFromDB.getName())
                .genre(bookFromDB.getGenre())
                .year(bookFromDB.getYear())
                .author(bookFromDB.getAuthor())
                .listOfComments(commentList)
                .build();
        BeanUtils.copyProperties(book, bookFromDB, "id");
        bookRepo.save(bookFromDB);
        return commentList;
    }

    @Override
    @Transactional
    public List<Comment> updateComment() {
        long idForBook = getIdForBook();
        long idComment = getCommentId(idForBook);
        helper.getEmptyString();
        Comment commentFromDB = commentRepo.findById(idComment).orElseThrow();
        System.out.println("Enter correct comment");
        String newComment = helper.getFromUser();
        Comment comment = Comment.builder().commentForBook(newComment).build();
        BeanUtils.copyProperties(comment, commentFromDB, "id");
        commentRepo.save(commentFromDB);
        return bookRepo.findById(idForBook).orElseThrow().getListOfComments();
    }

    @Override
    public List<Comment> deleteComment() {
        long idForBook = getIdForBook();
        long idForComment = getCommentId(idForBook);
        Comment comment = commentRepo.findById(idForComment).orElseThrow();
        commentRepo.deleteById(comment.getId());
        return bookRepo.findById(idForBook).orElseThrow().getListOfComments();
    }

    private int getIdForBook() {
        System.out.println("Enter name for book");
        String nameOfBook = helper.getFromUser();
        List<Book> listOfBooks = bookRepo.getBooksByName(nameOfBook);
        for (Book b : listOfBooks) {
            System.out.println(b);
        }
        System.out.println("Find id your book and enter it");
        return helper.getIdFromUser();
    }

    private int getCommentId(long idForBook) {

        for (Comment c : bookRepo.findById(idForBook).orElseThrow().getListOfComments()) {
            System.out.println(c);
        }
        System.out.println("Choose and enter id of comment");
        return helper.getIdFromUser();
    }
}
