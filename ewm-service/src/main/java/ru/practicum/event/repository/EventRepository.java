package ru.practicum.event.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.common.State;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.customRepository.CustomEventRepository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer>, CustomEventRepository {


    List<Event> findEventByIdIn(List<Integer> eventIds);

    List<Event> findEventByInitiatorId(int userId, Pageable pageable);

    List<Event> findEventByCategoryId(int categoryId);

    @Query("SELECT e from Event e " +
            "WHERE (:categories is null OR e.category.id IN :categories) " +
            "OR ((cast(:start as timestamp) Is null OR cast(:end as timestamp) Is null) " +
            "OR (cast(:start as timestamp) Is Not Null AND cast(:end as timestamp) Is not null " +
            "AND e.eventDate BETWEEN :start AND :end)) " +
            "AND (:text is null OR (lower(e.annotation) like %:text% or lower(e.description) like %:text%)) " +
            "AND e.paid = :paid")
    List<Event> getEventsWithSort(@Param("categories") List<Integer> categories,
                                  @Param("start") LocalDateTime start,
                                  @Param("end") LocalDateTime end,
                                  @Param("text") String text,
                                  boolean paid,
                                  Pageable pageable);

    @Query("SELECT e from Event e " +
            "WHERE (:users is null OR e.initiator.id IN :users) " +
            "AND (:states is null OR e.state IN :states) " +
            "AND (:categories is null OR e.category.id IN :categories)" +
            "AND ((cast(:start as timestamp) Is null OR cast(:end as timestamp) Is null) " +
            "OR (cast(:start as timestamp) Is Not Null AND cast(:end as timestamp) Is Not Null " +
            "AND e.eventDate BETWEEN :start AND :end))"
    )
    List<Event> getEventsFromAdmin(@Param("users") List<Integer> users,
                                   @Param("states") List<State> states,
                                   @Param("categories") List<Integer> categories,
                                   @Param("start") LocalDateTime start,
                                   @Param("end") LocalDateTime end,
                                   Pageable pageable);


}
