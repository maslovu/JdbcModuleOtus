package com.maslov.booksmaslov.dao;

import com.maslov.booksmaslov.domain.Comment;
import com.maslov.booksmaslov.repository.CommentRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
//@Import({CommentDao.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CommentDaoTest {
    private static final long ID_OF_COMMENT = 1;
    private static final String FIRST_COMM = "no";
    private static final String SECOND_COMMENT = "second comment";
    private static final String UPDATE_COMMENT = "update comment";
    private static final String ERROR_MESSAGE = "No comment for this ID";

    @Autowired
    private CommentRepo commentRepo;

    @Test
    void getCommentById() {
        Comment comment = commentRepo.findById(ID_OF_COMMENT).orElseThrow();

        assertThat(comment.getCommentForBook()).isEqualTo(FIRST_COMM);
    }

    @Test
    void createComment() {
        long commentId = commentRepo.save(new Comment(0, SECOND_COMMENT)).getId();

        assertThat(commentRepo.findById(commentId).orElseThrow().getCommentForBook()).isEqualTo(SECOND_COMMENT);
    }

    @Test
    void updateComment() {
        Comment commentFromDB = commentRepo.findById(ID_OF_COMMENT).get();
        BeanUtils.copyProperties(new Comment(0, UPDATE_COMMENT), commentFromDB, "id");
        commentRepo.save(commentFromDB);

        assertThat(commentRepo.findById(ID_OF_COMMENT).orElseThrow().getCommentForBook()).isEqualTo(UPDATE_COMMENT);
    }

    @Test
    void deleteComment() {
        commentRepo.delete(new Comment(ID_OF_COMMENT, FIRST_COMM));

        try {
            commentRepo.findById(ID_OF_COMMENT);
        } catch (Exception e) {
            assertEquals(ERROR_MESSAGE, e.getMessage());
        }
    }
}