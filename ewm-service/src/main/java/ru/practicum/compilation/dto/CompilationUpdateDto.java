package ru.practicum.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompilationUpdateDto {
    private List<Integer> events;
    @Value("false")
    private Boolean pinned;
    private String title;
}
