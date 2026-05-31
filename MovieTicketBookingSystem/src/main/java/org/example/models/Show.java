package org.example.models;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.strategies.PricingStrategy;

@Getter
@AllArgsConstructor
public class Show {
    private final String id;
    private final Movie movie;
    private final Screen screen;
    private final LocalDateTime startTime;
    private final PricingStrategy pricingStrategy;
}
