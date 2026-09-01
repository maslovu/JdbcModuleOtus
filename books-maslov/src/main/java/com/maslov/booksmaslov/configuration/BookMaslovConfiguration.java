package com.maslov.booksmaslov.configuration;

import com.maslov.booksmaslov.mapper.BookMapper;
import com.maslov.booksmaslov.mapper.CommentMapper;
import com.maslov.booksmaslov.repository.BookRepo;
import com.maslov.booksmaslov.repository.CommentRepo;
import com.maslov.booksmaslov.service.BookService;
import com.maslov.booksmaslov.service.CommentService;
import com.maslov.booksmaslov.service.impl.BookServiceImpl;
import com.maslov.booksmaslov.service.impl.CommentServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;

@Configuration
@EnableRetry
public class BookMaslovConfiguration {

    @Bean
    public BookService bookService(BookRepo bookRepo, BookMapper mapper) {
        return new BookServiceImpl(bookRepo, mapper);
    }

    @Bean
    public CommentService commentService(BookRepo bookRepo, CommentRepo commentRepo, CommentMapper mapper) {
        return new CommentServiceImpl(bookRepo, commentRepo, mapper);
    }
}
