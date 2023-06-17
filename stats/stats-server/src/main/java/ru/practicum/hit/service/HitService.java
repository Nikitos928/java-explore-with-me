package ru.practicum.hit.service;

import ru.practicum.hit.dto.HitDto;
import ru.practicum.hit.dto.HitInDto;

import java.time.LocalDateTime;
import java.util.List;

public interface HitService {

    List<HitDto> getHits(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);

    HitInDto saveNewHit(HitInDto hitDto);
}
