package com.maslov.booksmaslov.controller;

import com.maslov.booksmaslov.domain.Book;
import com.maslov.booksmaslov.model.BookDto;
import com.maslov.booksmaslov.service.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService service;

    public BookController(BookService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        var books = service.getAllBook();
        return ResponseEntity.ok(books);
    }

    @PostMapping("/create")
    public ResponseEntity<BookDto> createBook(@RequestBody BookDto book) {
        return ResponseEntity.ok(service.createBook(book));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable long id,
                                           @RequestBody BookDto bookDto) {
        Book updatedBook = service.updateBook(id, bookDto);

        return ResponseEntity.ok(updatedBook);
    }
}
