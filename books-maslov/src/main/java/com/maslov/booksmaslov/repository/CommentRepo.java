package com.maslov.booksmaslov.repository;

import com.maslov.booksmaslov.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepo extends JpaRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c WHERE c.book.id = :bookId")
    List<Comment> getCommentsForBookByBookId(@Param("bookId") long bookId);
}
