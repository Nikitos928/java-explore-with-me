package ru.practicum.hit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.hit.client.BaseClient;
import ru.practicum.hit.dto.HitInDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;


@Service
@Slf4j
public class HitClient extends BaseClient {
    @Autowired
    public HitClient(@Value("${stats-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public ResponseEntity<Object> getHits(LocalDateTime startLDT, LocalDateTime endLDT, List<String> uris, Boolean unique) {
        log.info("HitClient. Запрос на получение статистики: uris={},start={}, end={},  unique={}",
                uris, startLDT, endLDT, unique);

        if (startLDT == null || endLDT == null || startLDT.isAfter(endLDT)) {
            throw new IllegalArgumentException("Не задан временной промежуток.");
        }

        String start = startLDT.format(dateTimeFormatter);
        String end = endLDT.format(dateTimeFormatter);


        Map<String, Object> parameters = Map.of(
                "start", start,
                "end", end,
                "uris", String.join(",", uris),
                "unique", unique
        );
        return get("/stats?start={start}&end={end}&uris={uris}&unique={unique}", parameters);
    }

    public ResponseEntity<Object> saveNewHit(String ip, String uri, String app) {
        HitInDto hitInDto = HitInDto.builder()
                .id(0L)
                .ip(ip)
                .uri(uri)
                .app(app)
                .timestamp(LocalDateTime.now().format(dateTimeFormatter))
                .build();
        log.info("HitClient. Запрос на сохранение статистики: {}", hitInDto);
        return post("/hit", hitInDto);
    }
}
