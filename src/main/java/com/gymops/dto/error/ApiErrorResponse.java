package com.gymops.dto.error;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Map;

@Schema(description = "Standard error response returned for failed API requests.")
public class ApiErrorResponse {

    @Schema(description = "Time the error response was generated.")
    private final LocalDateTime timestamp;
    @Schema(description = "HTTP status code.", example = "400")
    private final int status;
    @Schema(description = "HTTP status reason phrase.", example = "Bad Request")
    private final String error;
    @Schema(description = "Safe, client-facing error message.")
    private final String message;
    @Schema(description = "Request path that produced the error.", example = "/api/members")
    private final String path;
    @Schema(description = "Field-level validation messages; empty when validation details do not apply.")
    private final Map<String, String> validationErrors;

    public ApiErrorResponse(
            LocalDateTime timestamp,
            int status,
            String error,
            String message,
            String path,
            Map<String, String> validationErrors) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.validationErrors = validationErrors;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getValidationErrors() {
        return validationErrors;
    }
}
