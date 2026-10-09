package com.gymops.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(
        title = "GymOps API",
        version = "1.0.0",
        description = "REST API for managing gym members, membership plans, memberships, payments, attendance, and dashboard metrics."))
public class OpenApiConfiguration {
}
