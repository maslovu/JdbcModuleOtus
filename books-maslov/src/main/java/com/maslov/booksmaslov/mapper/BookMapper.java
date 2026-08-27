package com.maslov.booksmaslov.mapper;

import com.maslov.booksmaslov.domain.Author;
import com.maslov.booksmaslov.domain.Book;
import com.maslov.booksmaslov.dto.BookDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, componentModel = "spring")
public interface BookMapper {

    @Mapping(source = "genre.id", target = "genreId")
    @Mapping(source = "year.id", target = "yearId")
    BookDto toDto(Book entity);

    // Метод преобразования Set<String> в одну строку через разделитель
    default String mapAuthors(Set<Author> authors) {
        if (authors == null || authors.isEmpty()) {
            return "";
        }
        return authors.stream()
                .map(Author::getName) // Берем имя автора
                .collect(Collectors.joining(", "));
    }

    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "genreId", target = "genre.id")
    @Mapping(target = "genre.name", ignore = true)
    @Mapping(source = "yearId", target = "year.id")
    @Mapping(target = "year.year", ignore = true)
    Book toEntity(BookDto bookDto);

    default Set<Author> stringToAuthors(String source) {
        if (source == null || source.isBlank()) {
            return Collections.emptySet(); }
        // Разделяем строку по запятой, убираем лишние пробелы и создаем объекты
        return Arrays.stream(source.split(","))
                .map(String::trim)
                .map(Author::new) // Предполагаем, что у Author есть конструктор (id, name)
                .collect(Collectors.toSet());
    }
}
