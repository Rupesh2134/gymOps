package com.gymops.controller;

import com.gymops.dto.membership.MembershipCreateRequest;
import com.gymops.dto.membership.MembershipResponse;
import com.gymops.dto.membership.MembershipUpdateRequest;
import com.gymops.dto.error.ApiErrorResponse;
import com.gymops.service.MembershipService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/memberships")
@Tag(name = "Memberships", description = "Manage member enrollment and membership status.")
public class MembershipController {

    private final MembershipService membershipService;

    public MembershipController(MembershipService membershipService) {
        this.membershipService = membershipService;
    }

    @GetMapping
    @Operation(summary = "List memberships")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Memberships retrieved"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public List<MembershipResponse> getAllMemberships() {
        return membershipService.getAllMemberships();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a membership")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Membership retrieved"),
            @ApiResponse(responseCode = "400", description = "Membership ID is invalid",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Membership not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public MembershipResponse getMembershipById(@PathVariable Long id) {
        return membershipService.getMembershipById(id);
    }

    @PostMapping
    @Operation(summary = "Create a membership")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Membership created"),
            @ApiResponse(responseCode = "400", description = "Request validation or business rule failure",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Member or plan not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "An active membership for this plan already exists",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<MembershipResponse> createMembership(
            @Valid @RequestBody MembershipCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(membershipService.createMembership(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a membership")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Membership updated"),
            @ApiResponse(responseCode = "400", description = "Request validation or business rule failure",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Membership, member, or plan not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public MembershipResponse updateMembership(
            @PathVariable Long id,
            @Valid @RequestBody MembershipUpdateRequest request) {
        return membershipService.updateMembership(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancel a membership")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Membership cancelled"),
            @ApiResponse(responseCode = "400", description = "Membership is already cancelled",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Membership not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteMembership(@PathVariable Long id) {
        membershipService.deleteMembership(id);
        return ResponseEntity.noContent().build();
    }
}
