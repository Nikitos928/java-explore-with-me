package ru.practicum.comment.mapper;

import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.CommentNewDto;
import ru.practicum.comment.model.Comment;
import ru.practicum.event.model.Event;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class CommentMapper {
    public static Comment mapToComment(User user, Event event, CommentNewDto commentNewDto) {
        Comment comment = new Comment();
        comment.setCreated(LocalDateTime.now());
        comment.setText(commentNewDto.getText());
        comment.setUser(user);
        comment.setEvent(event);
        return comment;
    }

    public static CommentDto mapToCommentDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setCreated(comment.getCreated());
        commentDto.setText(comment.getText());
        commentDto.setUser(UserMapper.mapToUserShortDto(comment.getUser()));
        commentDto.setEventId(comment.getEvent().getId());
        return commentDto;
    }

    public static List<CommentDto> mapToListCommentDto(List<Comment> comments) {
        List<CommentDto> commentDtos = comments.stream()
                .map(CommentMapper::mapToCommentDto)
                .collect(Collectors.toList());
        return commentDtos;
    }
}
