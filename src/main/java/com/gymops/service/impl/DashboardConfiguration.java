package com.gymops.service.impl;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class DashboardConfiguration {

    @Bean
    public Clock dashboardClock() {
        return Clock.systemDefaultZone();
    }
}
