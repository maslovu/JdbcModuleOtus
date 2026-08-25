package com.maslov.booksmaslov.configuration;

import com.maslov.booksmaslov.domain.Author;
import com.maslov.booksmaslov.domain.Genre;
import com.maslov.booksmaslov.domain.YearOfPublish;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;
import java.util.stream.Collectors;

import static org.modelmapper.config.Configuration.AccessLevel.PRIVATE;

@Configuration
public class ConfigurationMapper {

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
        return mapper;
    }
}
