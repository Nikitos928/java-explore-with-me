package ru.practicum.comment.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.comment.model.Comment;

import java.util.List;
import java.util.Map;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findCommentByEventId(Integer eventId);

    List<Comment> findCommentByEventId(Integer eventId, Pageable pageable);

    List<Comment> findCommentByEventIdIn(List<Integer> events);

    int countAllByEventId(Integer eventId);

}
