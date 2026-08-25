package com.maslov.booksmaslov.mapper;

import com.maslov.booksmaslov.domain.Book;
import com.maslov.booksmaslov.model.BookDto;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class BookMapper {

    private final ModelMapper mapper;

    public BookMapper(ModelMapper mapper) {
        this.mapper = mapper;
    }

    public Book toEntity(BookDto dto) {
        return Objects.isNull(dto) ? null : mapper.map(dto, Book.class);
    }

    public Book toEntity(BookDto dto, Book book) {
        if (Objects.isNull(dto) || Objects.isNull(book)) {
            return null;
        }
        mapper.map(dto, book);
        return book;
    }

    public BookDto toDto(Book entity) {
        return Objects.isNull(entity) ? null : mapper.map(entity, BookDto.class);
    }
}
