package ru.practicum.hit.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.hit.dto.HitDto;
import ru.practicum.hit.dto.HitInDto;
import ru.practicum.hit.service.HitService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
public class HitController {
    private final HitService hitService;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @GetMapping(path = "/stats")
    public List<HitDto> getHits(
            @RequestParam(name = "start", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
            @RequestParam(name = "end", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
            @RequestParam(name = "uris", required = false) List<String> uris,
            @RequestParam(name = "unique", defaultValue = "false") Boolean unique) {
        log.info("Запрос на получение статистики Stats. GET: uris={}, period start={}, end={}, unique is {}",
                uris, start, end, unique);
        List<HitDto> hits = hitService.getHits(start, end, uris, unique);
        log.info("Stats. GET: по запросу получено: {}", hits);
        return hits;
    }

    @PostMapping(path = "/hit")
    @ResponseStatus(value = HttpStatus.CREATED)
    public HitInDto saveNewHit(@RequestBody @Valid HitInDto hitInDto) {
        log.info("Stats: новый запрос {}", hitInDto);
        return hitService.saveNewHit(hitInDto);
    }
}
