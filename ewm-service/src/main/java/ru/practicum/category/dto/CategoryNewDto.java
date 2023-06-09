package ru.practicum.category.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryNewDto {
    @NotNull(message = "CategoryInDto. Field: name не задан")
    @NotBlank(message = "CategoryInDto. Field: name не может быть пустым или содержать только пробелы")
    @Size(max = 50)
    String name;
}
