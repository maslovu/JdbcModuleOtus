package com.maslov.booksmaslov;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.retry.annotation.EnableRetry;

@EnableCaching
@SpringBootApplication
@EnableRetry
public class BooksMaslovApplication {

    public static void main(String[] args) {
        SpringApplication.run(BooksMaslovApplication.class, args);
    }
}
