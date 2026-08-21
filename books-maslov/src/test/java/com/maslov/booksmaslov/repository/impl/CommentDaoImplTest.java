package com.maslov.booksmaslov.repository.impl;

import com.maslov.booksmaslov.domain.Comment;
import com.maslov.booksmaslov.exception.MaslovBookException;
import com.maslov.booksmaslov.repository.CommentDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@Import({CommentDaoImpl.class, BookDaoImpl.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CommentDaoImplTest {
    private static final long ID_OF_COMMENT = 1;
    private static final String FIRST_COMM = "first";
    private static final String SECOND_COMMENT = "second comment";
    private static final String UPDATE_COMMENT = "update comment";
    private static final String ERROR_MESSAGE = "No comment for this ID";

    @Autowired
    private CommentDao dao;

    @Test
    void getCommentById() {
        Comment comment = dao.getCommentById(ID_OF_COMMENT);

        assertThat(comment.getCommentForBook()).isEqualTo(FIRST_COMM);
    }

    @Test
    void updateComment() {
        dao.updateComment(new Comment(ID_OF_COMMENT, UPDATE_COMMENT));

        assertThat(dao.getCommentById(ID_OF_COMMENT).getCommentForBook()).isEqualTo(UPDATE_COMMENT);
    }

    @Test
    void deleteComment() {
        dao.deleteComment(new Comment(ID_OF_COMMENT, FIRST_COMM));

        try {
            dao.getCommentById(ID_OF_COMMENT);
        } catch (MaslovBookException e) {
            assertEquals(ERROR_MESSAGE, e.getMessage());
        }
    }
}