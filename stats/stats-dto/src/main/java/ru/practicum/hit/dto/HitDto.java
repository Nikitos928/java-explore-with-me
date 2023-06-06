package ru.practicum.hit.dto;

import lombok.Data;
import lombok.Value;

@Data
@Value
public class HitDto {
    String app;
    String uri;
    Long hits;
}
