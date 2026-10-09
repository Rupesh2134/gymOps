package com.gymops.controller;

import com.gymops.dto.attendance.AttendanceCreateRequest;
import com.gymops.dto.attendance.AttendanceResponse;
import com.gymops.dto.attendance.AttendanceUpdateRequest;
import com.gymops.dto.error.ApiErrorResponse;
import com.gymops.service.AttendanceService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestController
@RequestMapping("/api/attendance")
@Tag(name = "Attendance", description = "Record gym check-ins and check-outs.")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @PostMapping("/check-in")
    @Operation(summary = "Check in a member")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Attendance created"),
            @ApiResponse(responseCode = "400", description = "Request validation or business rule failure",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Member not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Attendance already exists for the member and date",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<AttendanceResponse> checkIn(
            @Valid @RequestBody AttendanceCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(attendanceService.createAttendance(request));
    }

    @PatchMapping("/{id}/check-out")
    @Operation(summary = "Check out a member")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Attendance updated with check-out time"),
            @ApiResponse(responseCode = "400", description = "Check-out time is missing or invalid",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Attendance not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public AttendanceResponse checkOut(
            @PathVariable Long id,
            @Valid @RequestBody AttendanceUpdateRequest request) {
        if (request.getCheckOutTime() == null) {
            throw new ResponseStatusException(BAD_REQUEST, "Check-out time is required");
        }

        AttendanceUpdateRequest checkOutRequest = new AttendanceUpdateRequest();
        checkOutRequest.setCheckOutTime(request.getCheckOutTime());
        return attendanceService.updateAttendance(id, checkOutRequest);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get an attendance record")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Attendance retrieved"),
            @ApiResponse(responseCode = "400", description = "Attendance ID is invalid",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Attendance not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public AttendanceResponse getAttendanceById(@PathVariable Long id) {
        return attendanceService.getAttendanceById(id);
    }

    @GetMapping
    @Operation(summary = "List attendance records")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Attendance records retrieved"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public List<AttendanceResponse> getAllAttendances() {
        return attendanceService.getAllAttendances();
    }
}
