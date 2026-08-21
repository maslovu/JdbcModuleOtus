package com.maslov.booksmaslov.service;

import com.maslov.booksmaslov.domain.Book;
import com.maslov.booksmaslov.domain.Comment;
import com.maslov.booksmaslov.repository.BookDao;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ServiceHelper {

    private final ScannerHelper helper;
    private final BookDao bookDao;

    public ServiceHelper(ScannerHelper helper, BookDao bookDao) {
        this.helper = helper;
        this.bookDao = bookDao;
    }

    public int getIdForBook() {
        System.out.println("Enter name for book");
        String nameOfBook = helper.getFromUser();
        List<Book> listOfBooks = bookDao.getBooksByName(nameOfBook);
        if (!listOfBooks.isEmpty()) {
            for (Book b : listOfBooks) {
                System.out.println(b);
            }
            System.out.println("Find id your book and enter it");
            return helper.getIdFromUser();
        } else {
            return 0;
        }
    }

    public int getCommentId(int idForBook) {
        System.out.println("Choose and enter id of comment");
        for (Comment c : bookDao.getBookById(idForBook).get().getListOfComments()) {
            System.out.println(c);
        }
        return helper.getIdFromUser();
    }
}
