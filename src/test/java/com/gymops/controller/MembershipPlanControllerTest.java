package com.gymops.controller;

import com.gymops.dto.membershipplan.MembershipPlanResponse;
import com.gymops.service.MembershipPlanService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MembershipPlanController.class)
class MembershipPlanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MembershipPlanService membershipPlanService;

    @Test
    void getAllPlans_returnsPlanDtos() throws Exception {
        MembershipPlanResponse plan = new MembershipPlanResponse();
        plan.setId(1L);
        plan.setName("Standard");
        when(membershipPlanService.getAllPlans()).thenReturn(List.of(plan));

        mockMvc.perform(get("/api/membership-plans"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Standard"));
    }

    @Test
    void getPlanById_returnsPlanDto() throws Exception {
        MembershipPlanResponse plan = new MembershipPlanResponse();
        plan.setId(1L);
        when(membershipPlanService.getPlanById(1L)).thenReturn(plan);

        mockMvc.perform(get("/api/membership-plans/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void createPlan_returnsCreatedAndValidatesRequest() throws Exception {
        MembershipPlanResponse plan = new MembershipPlanResponse();
        plan.setId(1L);
        plan.setName("Standard");
        when(membershipPlanService.createPlan(any())).thenReturn(plan);

        mockMvc.perform(post("/api/membership-plans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Standard","durationInMonths":1,"price":29.99}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));

        mockMvc.perform(post("/api/membership-plans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":" "}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updatePlan_returnsUpdatedDto() throws Exception {
        MembershipPlanResponse plan = new MembershipPlanResponse();
        plan.setId(1L);
        plan.setPrice(new BigDecimal("39.99"));
        when(membershipPlanService.updatePlan(eq(1L), any())).thenReturn(plan);

        mockMvc.perform(put("/api/membership-plans/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"price":39.99}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(39.99));
    }

    @Test
    void deletePlan_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/membership-plans/1"))
                .andExpect(status().isNoContent());

        verify(membershipPlanService).deletePlan(1L);
    }
}
