package ru.practicum.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import ru.practicum.validation.group.OnCreate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompilationInputDto {
    private List<Integer> events;
    @Value("false")
    private Boolean pinned;
    @NotBlank(message = "CompilationDto. Field: title не может быть пустым или содержать только пробелы", groups = OnCreate.class)
    @Size(max = 50)
    private String title;
}
