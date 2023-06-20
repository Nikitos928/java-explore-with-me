package ru.practicum.category.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryNewDto {
    @NotBlank(message = "CategoryInDto. Field: name не может быть пустым или содержать только пробелы")
    @Size(max = 255)
    String name;
}
