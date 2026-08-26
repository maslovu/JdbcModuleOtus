package com.maslov.booksmaslov.controller;

import com.maslov.booksmaslov.model.BookDto;
import com.maslov.booksmaslov.model.CommentDto;
import com.maslov.booksmaslov.model.CommentRequest;
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
import java.util.Set;

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
    public ResponseEntity<BookDto> getBookById(@PathVariable long id) {
        BookDto book = bookService.getBook(id);
        return ResponseEntity.ok(book);
    }

    @PostMapping
    public ResponseEntity<BookDto> createBook(@Valid @RequestBody BookDto book) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookService.createBook(book));
    }

    @PutMapping("/{bookId}")
    public ResponseEntity<BookDto> updateBook(@PathVariable long id,
                                              @Valid @RequestBody BookDto bookDto) {
        BookDto updatedBook = bookService.updateBook(id, bookDto);

        return ResponseEntity.ok(updatedBook);
    }

    @DeleteMapping("/{bookId}")
    public void deleteBook(@PathVariable long id) {
        bookService.delBook(id);
    }

    @GetMapping("/{bookId}/comments")
    public ResponseEntity<Set<CommentDto>> getCommentsForBook(@PathVariable long bookId) {
        Set<CommentDto> comments = commentService.getAllCommentForBook(bookId);
        return ResponseEntity.ok(comments);
    }

    @PostMapping("/{bookId}/comments")
    public ResponseEntity<CommentDto> createComment(@PathVariable long bookId,
                                                    @Valid @RequestBody CommentRequest comment) {
        CommentDto createdComment = commentService.createComment(comment, bookId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdComment);
    }

    @PutMapping("/comments/{commentId}")
    public ResponseEntity<CommentDto> updateComment(@PathVariable long commentId,
                                                    @Valid @RequestBody CommentRequest comment) {
        CommentDto updatedComment = commentService.updateComment(comment, commentId);
        return ResponseEntity.ok(updatedComment);
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
}
