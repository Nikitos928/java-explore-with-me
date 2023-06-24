package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventInputDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.service.EventService;
import ru.practicum.validation.group.OnCreate;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
@Validated
public class PrivateEventController {
    private final EventService eventService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{userId}/events")
    public List<EventShortDto> getEventsByUserId(
            @PathVariable int userId,
            @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(value = "size", defaultValue = "10") @Positive Integer size) {
        List<EventShortDto> eventShortDtos = eventService.getPrivateEventsByUserId(userId, from, size);
        log.info("API PrivateEvent. GET:  найдены события для пользователя с userId={} : {}", userId, eventShortDtos);
        return eventShortDtos;
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{userId}/events/{eventId}")
    public EventFullDto getEventBuEventIdAndUserId(@PathVariable int userId, @PathVariable int eventId) {
        EventFullDto eventFullDto = eventService.getPrivateEventByEventIdAndUserId(userId, eventId);
        log.info("API PrivateEvent. GET: Информация о событии с userId={}  и eventId={} : {}",
                userId, eventId, eventFullDto);
        return eventFullDto;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{userId}/events")
    @Validated({OnCreate.class})
    public EventFullDto saveNewEvent(@PathVariable int userId,
                                     @Valid @RequestBody EventInputDto eventNewDto) {
        if (eventNewDto.getPaid() == null) {
            eventNewDto.setPaid(false);
        }
        if (eventNewDto.getParticipantLimit() == null) {
            eventNewDto.setParticipantLimit(0);
        }
        if (eventNewDto.getRequestModeration() == null) {
            eventNewDto.setRequestModeration(true);
        }
        log.info("API PrivateEvent. POST параметры: userId={}, eventNewDto={}", userId, eventNewDto);
        EventFullDto eventFullDto = eventService.saveNewEvent(userId, eventNewDto);
        log.info("API PrivateEvent. POST: Добавлено событие: {}", eventFullDto);
        return eventFullDto;
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{userId}/events/{eventId}")
    public EventFullDto updateUser(@PathVariable(value = "userId") int userId,
                                   @PathVariable(value = "eventId") int eventId,
                                   @Valid @RequestBody EventInputDto eventUpdateDto) {
        log.info("API PrivateEvent. PATCH параметры: userId={}, eventId={}, eventUpdateDto={}",
                userId, eventId, eventUpdateDto);
        EventFullDto eventFullDto = eventService.updatePrivateEvent(userId, eventId, eventUpdateDto);
        log.info("API PrivateEvent. PATCH: Обновлены данные: {}", eventFullDto);
        return eventFullDto;
    }
}
