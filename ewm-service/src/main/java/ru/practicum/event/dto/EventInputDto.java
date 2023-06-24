package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import ru.practicum.common.StateAction;
import ru.practicum.validation.group.OnCreate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventInputDto {
    @NotBlank(message = "EventNewDto. Field: annotation не может быть пустым или содержать только пробелы", groups = OnCreate.class)
    @Size(min = 20, max = 2000)
    private String annotation;

    @NotNull(message = "EventNewDto. Field: category не задано", groups = OnCreate.class)
    private Integer category;

    @NotBlank(message = "EventNewDto. Field: description не может быть пустым или содержать только пробелы", groups = OnCreate.class)
    @Size(min = 20, max = 7000)
    private String description;

    @NotNull(message = "EventNewDto. Field: eventDate не задано", groups = OnCreate.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    @NotNull(message = "EventNewDto. Field: location не задано", groups = OnCreate.class)
    private Location location;

    @Value("false")
    private Boolean paid;

    @PositiveOrZero
    @Value("0")
    private Integer participantLimit;

    @Value("true")
    private Boolean requestModeration;

    @NotBlank(message = "EventNewDto. Field: title не может быть пустым или содержать только пробелы", groups = OnCreate.class)
    @Size(min = 3, max = 120)
    private String title;

    private StateAction stateAction;
}
