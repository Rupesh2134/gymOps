package com.gymops.exception;

import com.gymops.controller.MemberController;
import com.gymops.service.MemberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberController.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MemberService memberService;

    @Test
    void resourceNotFound_returnsStructuredNotFoundResponse() throws Exception {
        when(memberService.getMemberById(123L))
                .thenThrow(new ResourceNotFoundException("Member not found with id: 123"));

        mockMvc.perform(get("/api/members/123"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Member not found with id: 123"))
                .andExpect(jsonPath("$.path").value("/api/members/123"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.validationErrors").isEmpty());
    }

    @Test
    void duplicateResource_returnsConflictResponse() throws Exception {
        when(memberService.createMember(org.mockito.ArgumentMatchers.any()))
                .thenThrow(new DuplicateResourceException("Email already exists"));

        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"firstName":"Asha","phone":"+15551234567","joinDate":"2025-01-01"}
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Email already exists"));
    }

    @Test
    void businessRule_returnsBadRequestResponse() throws Exception {
        when(memberService.getMemberById(123L))
                .thenThrow(new BusinessRuleException("Member cannot be deleted again"));

        mockMvc.perform(get("/api/members/123"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Member cannot be deleted again"));
    }

    @Test
    void validationFailure_returnsFieldMessages() throws Exception {
        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"phone":"+15551234567"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Request validation failed"))
                .andExpect(jsonPath("$.validationErrors.firstName").value("First name is required"))
                .andExpect(jsonPath("$.validationErrors.joinDate").value("Join date is required"));
    }

    @Test
    void malformedJson_returnsSafeBadRequestResponse() throws Exception {
        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value(
                        "Request body is malformed or contains invalid values"));
    }

    @Test
    void invalidPathType_returnsBadRequestResponse() throws Exception {
        mockMvc.perform(get("/api/members/not-a-number"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Request contains a missing or invalid value"));
    }

    @Test
    void unexpectedException_returnsGenericResponseWithoutInternalDetails() throws Exception {
        when(memberService.getMemberById(123L))
                .thenThrow(new IllegalStateException("database password must not be exposed"));

        mockMvc.perform(get("/api/members/123"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("An unexpected error occurred"))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.not(
                        org.hamcrest.Matchers.containsString("database password"))))
                .andExpect(jsonPath("$.trace").doesNotExist());
    }
}
