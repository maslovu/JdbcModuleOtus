package com.maslov.booksmaslov.mapper;

import com.maslov.booksmaslov.domain.Comment;
import com.maslov.booksmaslov.dto.CommentDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "bookId", source = "book.id")
    CommentDto toDto(Comment request);
}
