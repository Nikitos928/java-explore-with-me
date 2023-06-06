package ru.practicum.hit.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.hit.dto.HitInDto;
import ru.practicum.hit.model.Hit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@UtilityClass
public class HitMapper {
    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Hit mapToHit(HitInDto hitDto) {
        Hit hit = new Hit();
        hit.setId(hitDto.getId());
        hit.setIp(hitDto.getIp());
        hit.setUri(hitDto.getUri());
        hit.setApp(hitDto.getApp());
        hit.setTimestamp(LocalDateTime.parse(hitDto.getTimestamp(), formatter));
        return hit;
    }

    public HitInDto mapToHitInDto(Hit hit) {
        return HitInDto.builder()
                .id(hit.getId())
                .ip(hit.getIp())
                .uri(hit.getUri())
                .app(hit.getApp())
                .timestamp(hit.getTimestamp().toString())
                .build();
    }
}
