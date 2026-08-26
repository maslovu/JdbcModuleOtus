package com.maslov.booksmaslov.mapper;

import com.maslov.booksmaslov.domain.Comment;
import com.maslov.booksmaslov.model.CommentDto;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class CommentMapper {

    private final ModelMapper mapper;

    public CommentMapper(ModelMapper mapper) {
        this.mapper = mapper;
    }

    public CommentDto toDto(Comment entity) {
        return Objects.isNull(entity) ? null : mapper.map(entity, CommentDto.class);
    }
}
