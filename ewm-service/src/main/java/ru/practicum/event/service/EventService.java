package ru.practicum.event.service;

import ru.practicum.common.SortMethod;
import ru.practicum.common.State;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventInputDto;
import ru.practicum.event.dto.EventShortDto;

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

    EventFullDto saveNewEvent(int userId, EventInputDto eventNewDto);

    EventFullDto updatePrivateEvent(int userId, int eventId, EventInputDto eventUpdateDto);

    EventFullDto updateAdminEvent(int eventId, EventInputDto eventUpdateDto);

}
