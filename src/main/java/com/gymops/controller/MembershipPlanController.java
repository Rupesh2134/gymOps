package com.gymops.controller;

import com.gymops.dto.membershipplan.MembershipPlanCreateRequest;
import com.gymops.dto.membershipplan.MembershipPlanResponse;
import com.gymops.dto.membershipplan.MembershipPlanUpdateRequest;
import com.gymops.dto.error.ApiErrorResponse;
import com.gymops.service.MembershipPlanService;
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
@RequestMapping("/api/membership-plans")
@Tag(name = "Membership plans", description = "Manage reusable membership plan definitions.")
public class MembershipPlanController {

    private final MembershipPlanService membershipPlanService;

    public MembershipPlanController(MembershipPlanService membershipPlanService) {
        this.membershipPlanService = membershipPlanService;
    }

    @GetMapping
    @Operation(summary = "List membership plans")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Membership plans retrieved"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public List<MembershipPlanResponse> getAllPlans() {
        return membershipPlanService.getAllPlans();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a membership plan")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Membership plan retrieved"),
            @ApiResponse(responseCode = "400", description = "Membership plan ID is invalid",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Membership plan not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public MembershipPlanResponse getPlanById(@PathVariable Long id) {
        return membershipPlanService.getPlanById(id);
    }

    @PostMapping
    @Operation(summary = "Create a membership plan")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Membership plan created"),
            @ApiResponse(responseCode = "400", description = "Request validation or business rule failure",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "A plan with the same name already exists",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<MembershipPlanResponse> createPlan(
            @Valid @RequestBody MembershipPlanCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(membershipPlanService.createPlan(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a membership plan")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Membership plan updated"),
            @ApiResponse(responseCode = "400", description = "Request validation or business rule failure",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Membership plan not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "A plan with the same name already exists",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public MembershipPlanResponse updatePlan(
            @PathVariable Long id,
            @Valid @RequestBody MembershipPlanUpdateRequest request) {
        return membershipPlanService.updatePlan(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deactivate a membership plan")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Membership plan deactivated"),
            @ApiResponse(responseCode = "400", description = "Membership plan is already inactive",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Membership plan not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<Void> deletePlan(@PathVariable Long id) {
        membershipPlanService.deletePlan(id);
        return ResponseEntity.noContent().build();
    }
}
