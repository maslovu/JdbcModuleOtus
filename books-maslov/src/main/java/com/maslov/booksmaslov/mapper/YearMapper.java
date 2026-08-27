package com.maslov.booksmaslov.mapper;

import com.maslov.booksmaslov.domain.YearOfPublish;
import com.maslov.booksmaslov.exception.ResourceNotFoundException;
import com.maslov.booksmaslov.repository.YearRepo;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
@RequiredArgsConstructor
public abstract class YearMapper {

    private YearRepo yearRepo;

    // 1. Ручной метод (MapStruct НЕ будет генерировать для него код, так как у него есть тело)
    public YearOfPublish toEntity(Long id) {
        if (id == null) {
            return null;
        }
        return yearRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Year", id));
    }

    // 2. Ручной метод преобразования сущности обратно в ID
    public Long toDto(YearOfPublish entity) {
        return entity != null ? entity.getId() : null;
    }
}
