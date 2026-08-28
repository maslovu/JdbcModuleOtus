package com.maslov.booksmaslov.controller;

import com.maslov.booksmaslov.dto.BookDto;
import com.maslov.booksmaslov.dto.CommentDto;
import com.maslov.booksmaslov.dto.CommentRequest;
import com.maslov.booksmaslov.service.BookService;
import com.maslov.booksmaslov.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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

    private final BookService bookService;
    private final CommentService commentService;

    public BookController(BookService service, CommentService commentService) {
        this.bookService = service;
        this.commentService = commentService;
    }

    @GetMapping
    public ResponseEntity<List<BookDto>> getAllBooks() {
        var books = bookService.getAllBook();
        return ResponseEntity.ok(books);
    }

    @GetMapping("/{bookId}")
    public ResponseEntity<BookDto> getBookById(@PathVariable long bookId) {
        BookDto book = bookService.getBook(bookId);
        return ResponseEntity.ok(book);
    }

    @GetMapping("/{bookId}/comments")
    public ResponseEntity<List<CommentDto>> getCommentsForBook(@PathVariable long bookId) {
        List<CommentDto> comments = commentService.getAllCommentForBook(bookId);
        return ResponseEntity.ok(comments);
    }

    @PostMapping
    public ResponseEntity<BookDto> createBook(@Valid @RequestBody BookDto book) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookService.createBook(book));
    }

    @PostMapping("/{bookId}/comment")
    public ResponseEntity<CommentDto> createComment(@PathVariable long bookId,
                                                    @Valid @RequestBody CommentRequest comment) {
        CommentDto createdComment = commentService.createComment(comment, bookId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdComment);
    }

    @PutMapping("/{bookId}")
    public ResponseEntity<BookDto> updateBook(@PathVariable long bookId,
                                              @Valid @RequestBody BookDto bookDto) {
        BookDto updatedBook = bookService.updateBook(bookId, bookDto);

        return ResponseEntity.ok(updatedBook);
    }

    @PutMapping("/comment/{commentId}")
    public ResponseEntity<CommentDto> updateComment(@PathVariable long commentId,
                                                    @Valid @RequestBody CommentRequest comment) {
        CommentDto updatedComment = commentService.updateComment(comment, commentId);
        return ResponseEntity.ok(updatedComment);
    }

    @DeleteMapping("/{bookId}")
    public void deleteBook(@PathVariable long bookId) {
        bookService.delBook(bookId);
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
}
