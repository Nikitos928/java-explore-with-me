package ru.practicum.event.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.comment.repository.CommentRepository;
import ru.practicum.common.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventInputDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventIds;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.RequestException;
import ru.practicum.hit.HitClient;
import ru.practicum.hit.dto.HitDto;
import ru.practicum.request.model.Request;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.dto.UserShortDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.common.StateAction.SEND_TO_REVIEW;


@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@ComponentScan(basePackages = {"hit"})
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final CategoryRepository categoryRepository;
    private final CommentRepository commentRepository;
    private final HitClient hitClient;
    private final JdbcTemplate jdbcTemplate;
    private static final String STATISTICS_APP = "evm-service";

    @Override
    public List<EventFullDto> getAdminEvents(List<Integer> users, List<State> states, List<Integer> categories,
                                             LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size) {
        Pageable pageable = FromSizeRequest.of(from, size, Sort.unsorted());
        List<Event> events = eventRepository.getEventsFromAdmin(
                users, states, categories, rangeStart, rangeEnd, pageable);
        return makeEventFullDtoList(events);
    }

    @Override
    public List<EventShortDto> getPublicEventsWithSort(String text, List<Integer> categories, boolean paid,
                                                       LocalDateTime start, LocalDateTime end, boolean onlyAvailable,
                                                       SortMethod sortMethod, int from, int size, String ip) {
        if (end != null && start != null) {
            if (!end.isAfter(start)) {
                throw new RequestException("Дата окончания события не может быть раньше начала");
            }
        }
        Pageable pageable;
        if (sortMethod == SortMethod.EVENT_DATE) {
            Sort sort = Sort.by("eventDate");
            pageable = FromSizeRequest.of(from, size, sort);
        } else {
            pageable = FromSizeRequest.of(from, size, Sort.unsorted());
        }
        if (text != null) {
            text = text.toLowerCase();
        }

        List<Event> events = eventRepository.getEventsWithSort(categories, start, end, text, paid, pageable);

        List<EventFullDto> eventFullDtos = makeEventFullDtoList(events);
        if (!onlyAvailable) {
            eventFullDtos.stream()
                    .filter(e -> e.getParticipantLimit() > e.getConfirmedRequests())
                    .collect(Collectors.toList());
        }

        List<EventShortDto> eventShortDtos = EventMapper.mapToListEventShortFromFullDto(eventFullDtos);

        if (sortMethod == SortMethod.VIEWS) {
            eventShortDtos = eventShortDtos.stream()
                    .sorted((e1, e2) -> {
                        if (e1.getViews() > e2.getViews())
                            return 1;
                        else if (e1.getViews() < e2.getViews())
                            return -1;
                        else return 0;
                    })
                    .collect(Collectors.toList());
        }
        hitClient.saveNewHit(ip, "/events", STATISTICS_APP);
        return eventShortDtos;
    }

    @Override
    public EventFullDto getPublicEventById(int eventId, String ip) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id=" + eventId + " не найдено."));

        if (!event.getState().equals(State.PUBLISHED)) {
            throw new NotFoundException("Событие с id=" + eventId + " не опубликовано.");
        }

        hitClient.saveNewHit(ip, "/events/" + eventId, STATISTICS_APP);


        EventFullDto eventFullDto = EventMapper.mapToEventFullDto(event);

        eventFullDto = setConfRequestEvent(List.of(eventFullDto), List.of(event.getId())).get(0);
        eventFullDto = setViewsEvent(List.of(eventFullDto), List.of("/events/" + event.getId())).get(0);
        eventFullDto.setComments(commentRepository.countAllByEventId(eventId));
        return eventFullDto;
    }

    @Override
    public EventFullDto getPrivateEventByEventIdAndUserId(int userId, int eventId) {
        Event event = checkingExistEventByUserId(userId, eventId);
        EventFullDto eventFullDto = EventMapper.mapToEventFullDto(event);

        eventFullDto = setConfRequestEvent(List.of(eventFullDto), List.of(event.getId())).get(0);
        eventFullDto = setViewsEvent(List.of(eventFullDto), List.of("/events/" + event.getId())).get(0);
        eventFullDto.setComments(commentRepository.countAllByEventId(eventId));
        return eventFullDto;
    }

    @Override
    public List<EventShortDto> getPrivateEventsByUserId(int userId, int from, int size) {
        Pageable pageable = FromSizeRequest.of(from, size, Sort.unsorted());
        List<Event> events = eventRepository.findEventByInitiatorId(userId, pageable);
        List<EventFullDto> eventFullDtos = makeEventFullDtoList(events);
        return EventMapper.mapToListEventShortFromFullDto(eventFullDtos);
    }

    @Transactional
    @Override
    public EventFullDto saveNewEvent(int userId, EventInputDto eventNewDto) {
        if (eventNewDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2L))) {
            log.info("Дата и время события {} - не может быть раньше, чем через два часа от текущего момента",
                    eventNewDto.getEventDate());
            throw new RequestException("Дата и время события не может быть раньше, чем через два часа от текущего момента");
        }

        User user = checkingExistUser(userId);
        Category category = checkingExistCategory(eventNewDto.getCategory());
        Event event = EventMapper.mapToEvent(user, category, eventNewDto);

        event.setCreatedOn(LocalDateTime.now());
        event.setState(State.PENDING);

        UserShortDto userShortDto = UserMapper.mapToUserShortDto(checkingExistUser(userId));
        CategoryDto categoryDto = CategoryMapper.mapToCategoryDto(checkingExistCategory(eventNewDto.getCategory()));
        Event newEvent = eventRepository.save(event);
        return EventMapper.mapToNewEventDto(userShortDto, categoryDto, newEvent);

    }

    @Transactional
    @Override
    public EventFullDto updatePrivateEvent(int userId, int eventId, EventInputDto eventUpdateDto) {
        Event event = checkingExistEventByUserId(userId, eventId);
        if (eventUpdateDto.getEventDate() != null) {
            if (eventUpdateDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2L))) {
                log.info("Дата и время события {} - не может быть раньше, чем через два часа от текущего момента",
                        eventUpdateDto.getEventDate());
                throw new RequestException("Дата и время события не может быть раньше, чем через два часа от текущего момента");
            }
        }
        if (event.getState() == State.PUBLISHED) {
            log.error("Нельзя изменить опубликованное событие.");
            throw new ConflictException("Нельзя изменить опубликованное событие.");
        }

        if (eventUpdateDto.getStateAction() != null) {
            switch (eventUpdateDto.getStateAction()) {
                case SEND_TO_REVIEW:
                    event.setState(State.PENDING);
                    break;
                case CANCEL_REVIEW:
                    event.setState(State.CANCELED);
                    break;
                default:
                    throw new RequestException("Не корректный статус");
            }
        }

        updateEvent(event, eventUpdateDto);

        EventFullDto eventFullDto = EventMapper.mapToEventFullDto(event);

        eventFullDto = setConfRequestEvent(List.of(eventFullDto), List.of(event.getId())).get(0);
        eventFullDto = setViewsEvent(List.of(eventFullDto), List.of("/events/" + event.getId())).get(0);
        eventFullDto.setComments(commentRepository.countAllByEventId(eventId));
        return eventFullDto;
    }

    @Transactional
    @Override
    public EventFullDto updateAdminEvent(int eventId, EventInputDto eventUpdateDto) {
        Event event = checkingExistEvent(eventId);

        if (eventUpdateDto.getEventDate() != null && event.getPublishedOn() != null) {
            if (eventUpdateDto.getEventDate().isAfter(event.getPublishedOn().plusHours(1))) {
                log.info("Дата начала события {} - не может быть раньше, чем за час от даты публикации",
                        eventUpdateDto.getEventDate());
                throw new ConflictException("Дата и время события не может быть раньше, чем через два часа от текущего момента");
            }
        }

        updateEvent(event, eventUpdateDto);

        EventFullDto eventFullDto = EventMapper.mapToEventFullDto(event);

        eventFullDto = setConfRequestEvent(List.of(eventFullDto), List.of(event.getId())).get(0);
        eventFullDto = setViewsEvent(List.of(eventFullDto), List.of("/events/" + event.getId())).get(0);
        eventFullDto.setComments(commentRepository.countAllByEventId(eventId));
        return eventFullDto;
    }

    private User checkingExistUser(int userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id=%s не найден", userId)));
    }

    private Event checkingExistEvent(int eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new ConflictException(String.format("Событие с id=%s не найдено", eventId)));
    }

    private Event checkingExistEventByUserId(int userId, int eventId) {
        Event event = checkingExistEvent(eventId);
        if (event.getInitiator().getId() != userId) {
            throw new RequestException("Пользователь не инициатор события.");
        }
        return event;
    }

    private Category checkingExistCategory(int catId) {
        return categoryRepository.findById(catId)
                .orElseThrow(() -> new ConflictException(String.format("Категория с id=%s не найдена", catId)));
    }

    private void updateEvent(Event event, EventInputDto eventUpdateDto) {
        if (eventUpdateDto.getEventDate() != null) {
            if (eventUpdateDto.getEventDate().isBefore(LocalDateTime.now())) {
                log.info("Нельзя изменить дату события {} на уже наступившую.",
                        eventUpdateDto.getEventDate());
                throw new RequestException("Нельзя изменить дату события на уже наступившую.");
            }
        }
        if (eventUpdateDto.getAnnotation() != null) {
            if (!eventUpdateDto.getAnnotation().isBlank()) {
                event.setAnnotation(eventUpdateDto.getAnnotation());
            }
        }
        if (eventUpdateDto.getCategory() != null) {
            event.setCategory(checkingExistCategory(eventUpdateDto.getCategory()));
        }
        if (eventUpdateDto.getDescription() != null) {
            if (!eventUpdateDto.getDescription().isBlank()) {
                event.setDescription(eventUpdateDto.getDescription());
            }
        }
        if (eventUpdateDto.getLocation() != null) {
            event.setLat(eventUpdateDto.getLocation().getLat());
            event.setLon(eventUpdateDto.getLocation().getLon());
        }
        if (eventUpdateDto.getPaid() != null) {
            event.setPaid(eventUpdateDto.getPaid());
        }
        if (eventUpdateDto.getParticipantLimit() != null) {
            event.setParticipantLimit(eventUpdateDto.getParticipantLimit());
        }
        if (eventUpdateDto.getRequestModeration() != null) {
            event.setRequestModeration(eventUpdateDto.getRequestModeration());
        }
        if (eventUpdateDto.getTitle() != null) {
            if (!eventUpdateDto.getTitle().isBlank()) {
                event.setTitle(eventUpdateDto.getTitle());
            }
        }

        if (eventUpdateDto.getStateAction() != null) {
            if (eventUpdateDto.getStateAction().equals(SEND_TO_REVIEW)) {
                event.setState(State.PENDING);
            }
        }
        if (eventUpdateDto.getStateAction() != null) {
            if (eventUpdateDto.getStateAction().equals(StateAction.PUBLISH_EVENT)) {
                if (event.getState() == State.PENDING) {
                    event.setState(State.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());

                } else {
                    if (event.getState() == State.CANCELED) {
                        log.error("Событие в состоянии CANCELED не может быть опубликовано.");
                        throw new ConflictException("Событие в состоянии CANCELED не может быть опубликовано.");
                    }
                    if (event.getState() == State.PUBLISHED) {
                        log.error("Событие уже опубликовано.");
                        throw new ConflictException("Событие уже опубликовано.");
                    }
                }
            }
        }
        if (eventUpdateDto.getStateAction() != null) {
            if (eventUpdateDto.getStateAction().equals(StateAction.REJECT_EVENT)) {
                if (event.getState() == State.PENDING) {
                    event.setState(State.CANCELED);
                } else if (event.getState() == State.PUBLISHED) {
                    throw new ConflictException("Событие уже опубликовано.");
                }
            }
        }
    }

    private List<EventFullDto> makeEventFullDtoList(List<Event> events) {
        List<EventFullDto> eventFullDtos = EventMapper.mapToListEventFullDto(events);

        List<Integer> ids = new ArrayList<>();
        List<String> uris = new ArrayList<>();
        events.forEach(event -> {
            ids.add(event.getId());
            uris.add("/events/" + event.getId());
        });

        eventFullDtos = setConfRequestEvent(eventFullDtos, ids);
        eventFullDtos = setViewsEvent(eventFullDtos, uris);
        eventFullDtos = setCommentsEvent(eventFullDtos, ids);
        return eventFullDtos;
    }

    private List<EventFullDto> setConfRequestEvent(List<EventFullDto> eventFullDtos, List<Integer> eventIds) {
        Map<Integer, List<Request>> confRequestMap = requestRepository.findRequestByEventIdInAndStatus(
                        eventIds, Status.CONFIRMED)
                .stream()
                .collect(Collectors.groupingBy(Request::getIdEvent));

        return eventFullDtos.stream()
                .map(eventFullDto -> setCountConfRequests(eventFullDto,
                        confRequestMap.getOrDefault(eventFullDto.getId(), Collections.emptyList()).size()))
                .collect(Collectors.toList());
    }

    private EventFullDto setCountConfRequests(EventFullDto eventFullDto, int countConfRequests) {
        eventFullDto.setConfirmedRequests(countConfRequests);
        return eventFullDto;
    }

    private List<EventFullDto> setViewsEvent(List<EventFullDto> eventFullDtos, List<String> uris) {
        LocalDateTime minStart = LocalDateTime.now().minusYears(100L);
        LocalDateTime maxEnd = LocalDateTime.now().plusYears(100L);
        Map<String, List<HitDto>> statViewsMap = hitClient.getHits(minStart, maxEnd, uris, true)
                .stream()
                .collect(Collectors.groupingBy(HitDto::getUri));

        for (EventFullDto event : eventFullDtos) {
            String key = "/events/" + event.getId();
            if (statViewsMap.get(key) != null) {
                setCountViews(event, statViewsMap.get(key).get(0).getHits());
            } else {
                event.setViews(0L);
            }
        }
        return eventFullDtos;
    }

    private void setCountViews(EventFullDto eventFullDto, Long countViews) {
        eventFullDto.setViews(countViews);
    }

    private List<EventFullDto> setCommentsEvent(List<EventFullDto> eventFullDtos, List<Integer> eventIds) {
        StringBuilder listString = new StringBuilder();
        for (int i = 0; i <= eventIds.size() - 1; i++) {
            if (i == 0) {
                listString.append("(").append(eventIds.get(i)).append(", ");
            } else if (i == eventIds.size() - 1) {
                listString.append(eventIds.get(i)).append(")");
            } else {
                listString.append(eventIds.get(i)).append(", ");
            }
        }

        String sql = "select event_id as event, count(id) as si  from comments where event_id in " + listString + " group by event_id";

        List<EventIds> eventIds1 = new ArrayList<>(jdbcTemplate.query(sql, (rs, rowNom) -> new EventIds(rs.getInt("event"), rs.getInt("si"))));

        Map<Integer, Integer> commentsMap = new HashMap<>();
        for (EventIds ids : eventIds1) {
            commentsMap.put(ids.getId(), ids.getSize());
        }
        return eventFullDtos.stream()
                .map(eventFullDto -> setComments(eventFullDto,
                        commentsMap.getOrDefault(eventFullDto.getId(), 0)))
                .collect(Collectors.toList());
    }

    private EventFullDto setComments(EventFullDto eventFullDto, int countComments) {
        eventFullDto.setComments(countComments);
        return eventFullDto;
    }

}
