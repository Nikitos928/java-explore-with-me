package ru.practicum.hit.dto;


import lombok.Builder;
import lombok.Data;
import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Value
@Builder
public class HitInDto {
    Integer id;
    @NotBlank
    @Size(max = 100)
    String ip;
    @NotBlank
    @Size(max = 100)
    String uri;
    @NotBlank
    @Size(max = 250)
    String app;
    String timestamp;
}
