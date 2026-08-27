package com.maslov.booksmaslov.mapper;

import com.maslov.booksmaslov.domain.Genre;
import com.maslov.booksmaslov.exception.ResourceNotFoundException;
import com.maslov.booksmaslov.repository.GenreRepo;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
@RequiredArgsConstructor
public abstract class GenreMapper {

    // Внедряем репозиторий в абстрактный класс
    private GenreRepo genreRepo;

    // Пишем ручную бизнес-логику в default (или обычном) методе
    public Genre toEntity(Long id) {
        if (id == null) {
            return null;
        }
        return genreRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Genre", id));
    }

    public Long toDto(Genre entity) {
        return entity != null ? entity.getId() : null;
    }
}
