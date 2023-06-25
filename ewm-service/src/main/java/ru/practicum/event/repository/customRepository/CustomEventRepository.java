package ru.practicum.event.repository.customRepository;

import java.util.List;
import java.util.Map;

public interface CustomEventRepository {
    Map<Integer, Integer> customFindMethod(List<Integer> ids);
}
