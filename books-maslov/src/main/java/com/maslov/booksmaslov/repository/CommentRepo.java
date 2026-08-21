package com.maslov.booksmaslov.repository;

import com.maslov.booksmaslov.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepo extends JpaRepository<Comment, Long> {

}
