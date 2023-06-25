package ru.practicum.event.repository.customRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CustomEventRepositoryImpl implements CustomEventRepository {


    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Map<Integer, Integer> customFindMethod(List<Integer> eventIds) {
        StringBuilder listString = new StringBuilder();
        if (eventIds.size() == 1) {
            listString.append("(").append(eventIds.get(0)).append(")");
        } else {
            for (int i = 0; i <= eventIds.size() - 1; i++) {
                if (i == 0) {
                    listString.append("(").append(eventIds.get(i)).append(", ");
                } else if (i == eventIds.size() - 1) {
                    listString.append(eventIds.get(i)).append(")");
                } else {
                    listString.append(eventIds.get(i)).append(", ");
                }
            }
        }

        String sql = "select c.event.id as event, " +
                "count(c.id) as si  " +
                "from Comment c " +
                "where c.event.id in " + listString +
                " group by c.event.id";

        return entityManager.createQuery(sql, Tuple.class).getResultStream().collect(Collectors.toMap(
                tuple -> ((Number) tuple.get("event")).intValue(),
                tuple -> ((Number) tuple.get("si")).intValue()));
    }
}
