package ru.practicum.event.service;

import ru.practicum.common.SortMethod;
import ru.practicum.common.State;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventNewDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.EventUpdateDto;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {

    List<EventFullDto> getAdminEvents(List<Integer> users, List<State> states, List<Integer> categories,
                                      LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                      int from, int size);

    List<EventShortDto> getPublicEventsWithSort(String text, List<Integer> categories, boolean paid,
                                                LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                boolean onlyAvailable, SortMethod sort, int from,
                                                int size, String ip);

    EventFullDto getPublicEventById(int eventId, String ip);

    List<EventShortDto> getPrivateEventsByUserId(int userId, int from, int size);

    EventFullDto getPrivateEventByEventIdAndUserId(int userId, int eventId);

    EventFullDto saveNewEvent(int userId, EventNewDto eventNewDto);

    EventFullDto updatePrivateEvent(int userId, int eventId, EventUpdateDto eventUpdateDto);

    EventFullDto updateAdminEvent(int eventId, EventUpdateDto eventUpdateDto);

}
