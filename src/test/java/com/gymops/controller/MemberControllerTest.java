package com.gymops.controller;

import com.gymops.dto.member.MemberResponse;
import com.gymops.service.MemberService;
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

@WebMvcTest(MemberController.class)
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MemberService memberService;

    @Test
    void getAllMembers_returnsMemberDtos() throws Exception {
        MemberResponse member = new MemberResponse();
        member.setId(1L);
        member.setFirstName("Asha");
        when(memberService.getAllMembers()).thenReturn(List.of(member));

        mockMvc.perform(get("/api/members"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].firstName").value("Asha"));
    }

    @Test
    void getMemberById_returnsMemberDto() throws Exception {
        MemberResponse member = new MemberResponse();
        member.setId(1L);
        when(memberService.getMemberById(1L)).thenReturn(member);

        mockMvc.perform(get("/api/members/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void createMember_returnsCreatedAndValidatesRequest() throws Exception {
        MemberResponse member = new MemberResponse();
        member.setId(1L);
        member.setFirstName("Asha");
        when(memberService.createMember(any())).thenReturn(member);

        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"firstName":"Asha","phone":"+15551234567","joinDate":"2025-01-01"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));

        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"phone":"+15551234567"}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateMember_returnsUpdatedDto() throws Exception {
        MemberResponse member = new MemberResponse();
        member.setId(1L);
        member.setFirstName("Updated");
        when(memberService.updateMember(eq(1L), any())).thenReturn(member);

        mockMvc.perform(put("/api/members/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"firstName":"Updated"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Updated"));
    }

    @Test
    void deleteMember_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/members/1"))
                .andExpect(status().isNoContent());

        verify(memberService).deleteMember(1L);
    }
}
