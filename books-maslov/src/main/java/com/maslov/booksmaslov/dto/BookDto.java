package com.maslov.booksmaslov.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookDto {

    public BookDto(long id, String title, String authors) {
        this.id = id;
        this.title = title;
        this.authors = authors;
    }

    private long id;

    @NotBlank(message = "Название книги не может быть пустым")
    @Size(max = 255, message = "Название слишком длинное")
    private String title;

    @NotBlank(message = "У книги должен быть указан хотя бы один автор")
    private String authors;

    @NotBlank(message = "Год издания должен быть указан")
    private String year;

    @NotBlank(message = "Жанр должен быть указан")
    private String genre;
}
