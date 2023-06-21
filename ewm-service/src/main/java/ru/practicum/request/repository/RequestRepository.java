package ru.practicum.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.common.Status;
import ru.practicum.request.model.Request;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Integer> {
    List<Request> findRequestByRequesterId(int userId);

    List<Request> findAllByIdIn(List<Integer> requestIds);

    List<Request> findRequestByEventId(int eventId);

    List<Request> findRequestByEventIdAndRequesterId(int eventId, int userId);


    List<Request> findRequestByEventIdInAndStatus(List<Integer> eventIds, Status status);

    @Query("select count (r.id) from Request r where r.event.id = ?1 and r.status = ?2")
    Integer getCountConfirmedRequest(Integer eventId, Status state);

}
