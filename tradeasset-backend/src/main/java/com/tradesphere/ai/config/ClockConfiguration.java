package com.tradesphere.ai.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.Clock;

/**
 * The one source of time (REQ-TIM-002). Code reads instants from the injected {@link Clock}; it
 * never calls {@code now()} inline. Calendar dates are derived with
 * {@link com.tradesphere.ai.shared.domain.time.BusinessCalendar}.
 */
@Configuration
@EnableScheduling
public class ClockConfiguration {

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }
}
