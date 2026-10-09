package com.gymops.controller;

import com.gymops.dto.membership.MembershipResponse;
import com.gymops.service.MembershipService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

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

@WebMvcTest(MembershipController.class)
class MembershipControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MembershipService membershipService;

    @Test
    void getAllMemberships_returnsMembershipDtos() throws Exception {
        MembershipResponse membership = new MembershipResponse();
        membership.setId(1L);
        membership.setMemberName("Asha");
        when(membershipService.getAllMemberships()).thenReturn(List.of(membership));

        mockMvc.perform(get("/api/memberships"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].memberName").value("Asha"));
    }

    @Test
    void getMembershipById_returnsMembershipDto() throws Exception {
        MembershipResponse membership = new MembershipResponse();
        membership.setId(1L);
        when(membershipService.getMembershipById(1L)).thenReturn(membership);

        mockMvc.perform(get("/api/memberships/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void createMembership_returnsCreatedAndValidatesRequest() throws Exception {
        MembershipResponse membership = new MembershipResponse();
        membership.setId(1L);
        membership.setPlanName("Standard");
        when(membershipService.createMembership(any())).thenReturn(membership);

        mockMvc.perform(post("/api/memberships")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"memberId":1,"planId":2,"startDate":"2025-01-01"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));

        mockMvc.perform(post("/api/memberships")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"memberId":1}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateMembership_returnsUpdatedDto() throws Exception {
        MembershipResponse membership = new MembershipResponse();
        membership.setId(1L);
        membership.setPlanName("Premium");
        when(membershipService.updateMembership(eq(1L), any())).thenReturn(membership);

        mockMvc.perform(put("/api/memberships/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"planId":3}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.planName").value("Premium"));
    }

    @Test
    void deleteMembership_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/memberships/1"))
                .andExpect(status().isNoContent());

        verify(membershipService).deleteMembership(1L);
    }
}
