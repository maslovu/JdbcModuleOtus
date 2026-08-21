package com.maslov.booksmaslov.shell;

import com.maslov.booksmaslov.service.BookService;
import com.maslov.booksmaslov.service.CommentService;
import lombok.AllArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
@AllArgsConstructor
public class CommandForShell {
    private BookService service;
    private CommentService commentService;

    @ShellMethod(value = "Get book", key = {"g", "getbook"})
    public void getBook() {
        service.getBook();
    }

    @ShellMethod(value = "Get all book", key = {"getall"})
    public void getAllBook() {
        service.getAllBook();
    }

    @ShellMethod(value = "Create book", key = {"createBook", "cr"})
    public void createBook() {
        service.createBook();
    }

    @ShellMethod(value = "Update book", key = {"updateBook", "upd"})
    public void updateBook() {
        service.updateBook();
    }

    @ShellMethod(value = "Delete book", key = {"d", "delbook"})
    public void delBook() {
        service.delBook();
    }

    @ShellMethod(value = "create comment", key = {"newcomment"})
    public void createComment() {
        commentService.createComment();
    }

    @ShellMethod(value = "update comment", key = {"updcomment"})
    public void updateComment() {
        commentService.updateComment();
    }
}
