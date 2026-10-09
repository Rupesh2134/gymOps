package com.gymops.controller;

import com.gymops.dto.dashboard.DashboardSummaryResponse;
import com.gymops.dto.error.ApiErrorResponse;
import com.gymops.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@Tag(name = "Dashboard", description = "Retrieve aggregate gym operations metrics.")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/summary")
    @Operation(summary = "Get dashboard summary",
            description = "Returns member, membership, attendance, and payment metrics. Expiring memberships end today through seven days ahead, inclusive; monthly revenue includes paid payments in the current calendar month.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dashboard summary retrieved"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public DashboardSummaryResponse getSummary() {
        return dashboardService.getSummary();
    }
}
