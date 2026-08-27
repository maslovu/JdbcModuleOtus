package com.maslov.booksmaslov.mapper;

import com.maslov.booksmaslov.domain.Author;
import com.maslov.booksmaslov.domain.Book;
import com.maslov.booksmaslov.domain.Genre;
import com.maslov.booksmaslov.domain.YearOfPublish;
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

    default String mapYear(YearOfPublish year) {
        if (year == null) {
            return null;
        }
        return year.getYear();
    }

    default String mapGenre(Genre genre) {
        if (genre == null) {
            return null;
        }
        return genre.getName();
    }

    @Mapping(target = "comments", ignore = true)
    Book toEntity(BookDto bookDto);

    default Genre mapString(String genre) {
        if (genre == null) {
            return null;
        }
        return new Genre(genre);
    }

    default YearOfPublish mapStringToYearOfPublish(String year) {
        if (year == null) {
            return null;
        }
        return new YearOfPublish(year);
    }

    default Set<Author> stringToAuthors(String source) {
        if (source == null || source.isBlank()) {
            return Collections.emptySet(); }
        // Разделяем строку по запятой, убираем лишние пробелы и создаем объекты
        return Arrays.stream(source.split(","))
                .map(String::trim)
                .map(Author::new) // Предполагаем, что у Author есть конструктор (id, name)
                .collect(Collectors.toSet()); }
}
