package ru.practicum.category.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryNewDto {
    @NotNull(message = "CategoryInDto. Field: name не задан")
    @NotBlank(message = "CategoryInDto. Field: name не может быть пустым или содержать только пробелы")
    String name;
}
