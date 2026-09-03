package com.maslov.booksmaslov.mapper;

import com.maslov.booksmaslov.domain.Genre;
import com.maslov.booksmaslov.domain.YearOfPublish;
import com.maslov.booksmaslov.exception.ResourceNotFoundException;
import com.maslov.booksmaslov.repository.GenreRepo;
import com.maslov.booksmaslov.repository.YearRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EntityLoaderContext {

    private final GenreRepo genreRepo;
    private final YearRepo yearRepo;

    public Genre loadGenre(Long id) {
        if (id == null) return null;
        return genreRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Genre not found: ", id));
    }

    public YearOfPublish loadYear(Long id) {
        if (id == null) return null;
        return yearRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Year not found: ", id));
    }
}
