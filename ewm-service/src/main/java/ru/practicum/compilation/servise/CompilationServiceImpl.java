package ru.practicum.compilation.servise;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.common.FromSizeRequest;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.CompilationNewDto;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.NotFoundException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compRepository;
    private final EventRepository eventRepository;

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, int from, int size) {
        Pageable pageable = FromSizeRequest.of(from, size, Sort.unsorted());
        List<Compilation> compilations = compRepository.findCompilationByPinnedIs(pinned, pageable);
        return CompilationMapper.mapToListCompilationDto(compilations);
    }

    @Override
    public CompilationDto getCompilationById(int compId) {
        Compilation compilation = checkingExistCompilation(compId);
        return CompilationMapper.mapToCompilationDto(compilation);
    }

    @Transactional
    @Override
    public CompilationDto saveNewCompilation(CompilationNewDto compilationNewDto) {
        Compilation compilation = new Compilation();
        compilation.setPinned(compilationNewDto.getPinned());
        compilation.setTitle(compilationNewDto.getTitle());
        if (compilationNewDto.getEvents() != null) {
            compilation.setEvents(new HashSet<>(new ArrayList<>(eventRepository.findEventByIdIn(compilationNewDto.getEvents()))));
        }
        compRepository.save(compilation);
        return CompilationMapper.mapToCompilationDto(compilation);
    }

    @Transactional
    @Override
    public CompilationDto updateCompilation(int compId, CompilationNewDto compilationUpdateDto) {
        Compilation updateCompilation = checkingExistCompilation(compId);

        if (compilationUpdateDto.getPinned() != null) {
            updateCompilation.setPinned(compilationUpdateDto.getPinned());
        }

        if (compilationUpdateDto.getTitle() != null) {
            updateCompilation.setTitle(compilationUpdateDto.getTitle());
        }

        if ((compilationUpdateDto.getEvents() != null) && !compilationUpdateDto.getEvents().isEmpty()) {
            updateCompilation.setEvents(new HashSet<>(new ArrayList<>(eventRepository.findEventByIdIn(compilationUpdateDto.getEvents()))));
        }
        return CompilationMapper.mapToCompilationDto(updateCompilation);
    }

    @Transactional
    @Override
    public void deleteCompilationById(int compId) {
        if (!compRepository.existsById(compId)) {
            throw new NotFoundException(String.format("Подборка с id=%s не найдена", compId));
        }
        compRepository.deleteById(compId);
    }

    private Compilation checkingExistCompilation(int compId) {
        return compRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException(String.format("Подборка с id=%s не найдена", compId)));
    }
}
