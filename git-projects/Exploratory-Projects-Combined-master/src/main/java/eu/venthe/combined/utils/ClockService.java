package eu.venthe.combined.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ClockService {
    private final Clock clock = Clock.systemUTC();

    public LocalDateTime now() {
        return LocalDateTime.now(clock);
    }
}
