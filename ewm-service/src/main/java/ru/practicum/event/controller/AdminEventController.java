package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.common.State;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventInputDto;
import ru.practicum.event.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/events")
public class AdminEventController {
    private final EventService eventService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<EventFullDto> getEvents(
            @RequestParam(value = "users", required = false) List<Integer> users,
            @RequestParam(value = "states", required = false) List<State> states,
            @RequestParam(value = "categories", required = false) List<Integer> categories,
            @RequestParam(value = "rangeStart", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            LocalDateTime rangeStart,
            @RequestParam(value = "rangeEnd", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            LocalDateTime rangeEnd,
            @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(value = "size", defaultValue = "10") @Positive Integer size) {

        log.info("API AdminEvent. GET: параметры запроса users={}, states={}, categories={}, Start={}, End={}",
                users, states, categories, rangeStart, rangeEnd);
        log.info("from = {} size = {}", from, size);
        List<EventFullDto> eventFullDtos = eventService.getAdminEvents(users, states, categories,
                rangeStart, rangeEnd, from, size);
        log.info("API AdminEvent. GET: найдено событий={} : {}", eventFullDtos.size(), eventFullDtos);
        return eventFullDtos;
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@PathVariable int eventId,
                                    @Valid @RequestBody EventInputDto eventUpdateDto) {
        log.info("API AdminEvent. PATCH: eventId={}, eventUpdateDto={}", eventId, eventUpdateDto);

        EventFullDto updateEvent = eventService.updateAdminEvent(eventId, eventUpdateDto);
        log.info("API AdminEvent. PATCH: обновлены данные: {}", updateEvent);
        return updateEvent;
    }
}
