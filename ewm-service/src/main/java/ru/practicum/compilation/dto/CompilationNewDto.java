package ru.practicum.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompilationNewDto {
    private List<Integer> events;
    @Value("false")
    private Boolean pinned;
    @NotNull(message = "CompilationNewDto. Field: title не задано")
    @NotBlank(message = "CompilationDto. Field: title не может быть пустым или содержать только пробелы")
    @Size(max = 50)
    private String title;
}
