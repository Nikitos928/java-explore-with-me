package ru.practicum.hit.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Value;

@Data
@Value
@Builder
public class HitDto {
    String app;
    String uri;
    Long hits;
}
