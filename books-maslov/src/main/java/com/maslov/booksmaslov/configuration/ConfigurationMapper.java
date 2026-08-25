package com.maslov.booksmaslov.configuration;

import com.maslov.booksmaslov.domain.Author;
import com.maslov.booksmaslov.domain.Book;
import com.maslov.booksmaslov.domain.Genre;
import com.maslov.booksmaslov.domain.YearOfPublish;
import com.maslov.booksmaslov.model.BookDto;
import com.maslov.booksmaslov.repository.AuthorRepo;
import com.maslov.booksmaslov.repository.GenreRepo;
import com.maslov.booksmaslov.repository.YearRepo;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.modelmapper.config.Configuration.AccessLevel.PRIVATE;

@Configuration
public class ConfigurationMapper {

    @Autowired
    private AuthorRepo authorRepo;
    @Autowired
    private GenreRepo genreRepo;
    @Autowired
    private YearRepo yearRepo;

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();

        mapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setFieldMatchingEnabled(true)
                .setSkipNullEnabled(true)
                .setFieldAccessLevel(PRIVATE);
        // 1. Создаем кастомный конвертер из Set<Authors> в String
        Converter<Set<Author>, String> authorsSetToString = context -> {
            Set<Author> source = context.getSource();
            if (source == null || source.isEmpty()) {
                return "";
            }
            // Вытаскиваем имя каждого автора и склеиваем через запятую
            return source.stream()
                    .map(Author::getName) // Предполагаем, что метод называется getName()
                    .collect(Collectors.joining(", "));
        };
        Converter<Genre, String> genreToString = mappingContext -> {
            Genre genre = mappingContext.getSource();
            if (genre == null) {
                return null;
            }
            return genre.getName();
        };
        Converter<YearOfPublish, String> yearOfPublishToString = mappingContext -> {
            YearOfPublish year = mappingContext.getSource();
            if (year == null) {
                return null;
            }
            return year.getYear();
        };

        // 2. Регистрируем конвертер в ModelMapper
        mapper.addConverter(authorsSetToString);
        mapper.addConverter(genreToString);
        mapper.addConverter(yearOfPublishToString);

        // Конвертер: из String (из DTO) в Set<Author> (в Entity)
        Converter<String, Set<Author>> authorsStringToSet = context -> {
            String source = context.getSource();
            if (source == null || source.isBlank()) {
                return null;
            }

            return Stream.of(source.split(","))
                    .map(String::trim) // Убираем лишние пробелы вокруг имен
                    .map(name -> authorRepo.getByName(name).orElseGet(() -> new Author(name)))
                    .collect(Collectors.toSet());
        };

        Converter<String, Genre> genreStringToGenre = mappingContext -> {
            String source = mappingContext.getSource();

            if (source == null || source.isBlank()) {
                return null;
            }

            return genreRepo.getGenreByName(source).orElseGet(() -> {
                Genre newGenre = new Genre(source);
                return genreRepo.createGenre(newGenre);
            });
        };

        Converter<String, YearOfPublish> yearStringToYear = mappingContext -> {
            String source = mappingContext.getSource();
            if (source == null || source.isBlank()) {
                return null;
            }
            return yearRepo.getYearByDate(source).orElseGet(() -> {
                YearOfPublish newYear = new YearOfPublish(source);
                return yearRepo.createYear(newYear);
            });
        };

        // Настраиваем правила маппинга для класса Book
        TypeMap<BookDto, Book> dtoToEntityMap = mapper.getTypeMap(BookDto.class, Book.class);
        if (dtoToEntityMap == null) {
            dtoToEntityMap = mapper.emptyTypeMap(BookDto.class, Book.class);
        }
        dtoToEntityMap.addMappings(m -> {
            m.using(authorsStringToSet).map(BookDto::getAuthors, Book::setAuthors);
            m.using(genreStringToGenre).map(BookDto::getGenre, Book::setGenre);
            m.using(yearStringToYear).map(BookDto::getYear, Book::setYear);
            m.map(BookDto::getTitle, Book::setTitle);
        });

        return mapper;
    }
}
