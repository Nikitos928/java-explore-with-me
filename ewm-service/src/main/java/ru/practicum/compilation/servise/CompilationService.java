package ru.practicum.compilation.servise;

import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.CompilationInputDto;

import java.util.List;

public interface CompilationService {
    List<CompilationDto> getCompilations(Boolean pinned, int from, int size);

    CompilationDto getCompilationById(int compId);

    CompilationDto saveNewCompilation(CompilationInputDto compilationNewDto);

    CompilationDto updateCompilation(int compId, CompilationInputDto compilationUpdateDto);

    void deleteCompilationById(int compId);
}
