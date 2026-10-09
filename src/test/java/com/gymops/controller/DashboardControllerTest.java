package com.gymops.controller;

import com.gymops.dto.dashboard.DashboardSummaryResponse;
import com.gymops.service.DashboardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:gymops-openapi;MODE=MySQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=false"
})
@AutoConfigureMockMvc
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DashboardService dashboardService;

    @Test
    void getSummary_returnsDashboardMetrics() throws Exception {
        when(dashboardService.getSummary()).thenReturn(
                new DashboardSummaryResponse(10, 7, 3, 5, 4, 2, 6, new BigDecimal("125.50"), 3));

        mockMvc.perform(get("/api/dashboard/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalMembers").value(10))
                .andExpect(jsonPath("$.activeMemberships").value(5))
                .andExpect(jsonPath("$.expiringMemberships").value(2))
                .andExpect(jsonPath("$.monthlyRevenue").value(125.50))
                .andExpect(jsonPath("$.pendingPayments").value(3));
    }

    @Test
    void openApiEndpoint_exposesGymOpsMetadataAndDocumentedRoute() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.title").value("GymOps API"))
                .andExpect(jsonPath("$.info.version").value("1.0.0"))
                .andExpect(jsonPath("$.paths['/api/dashboard/summary'].get.summary")
                        .value("Get dashboard summary"));
    }

    @Test
    void swaggerUiPath_redirectsToTheConfiguredUi() throws Exception {
        mockMvc.perform(get("/swagger-ui.html"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location",
                        org.hamcrest.Matchers.containsString("/swagger-ui/index.html")));
    }
}
