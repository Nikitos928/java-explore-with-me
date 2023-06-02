package ru.practicum.hit.service;

import ru.practicum.hit.dto.HitDto;
import ru.practicum.hit.dto.HitInDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.hit.repository.HitRepository;
import ru.practicum.hit.mapper.HitMapper;
import ru.practicum.hit.model.Hit;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HitServiceImpl implements HitService {
    private final HitRepository hitRepository;

    @Override
    public List<HitDto> getHits(LocalDateTime start, LocalDateTime end, String[] uri, boolean unique) {
        if (uri == null) {
            if (unique) {
                return hitRepository.getHitsUniqueIp(start, end);
            }
            return hitRepository.getHits(start, end);
        }

        if (unique) {
            return hitRepository.getHitsWithUriUniqueIp(start, end, uri);
        }
        return hitRepository.getHitsWithUri(start, end, uri);
    }

    @Transactional
    @Override
    public HitInDto saveNewHit(HitInDto hitDto) {
        Hit newHit = hitRepository.save(HitMapper.mapToHit(hitDto));
        return HitMapper.mapToHitInDto(newHit);
    }
}
