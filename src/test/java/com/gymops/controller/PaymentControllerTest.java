package com.gymops.controller;

import com.gymops.dto.payment.PaymentResponse;
import com.gymops.service.PaymentService;
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

@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PaymentService paymentService;

    @Test
    void getAllPayments_returnsPaymentDtos() throws Exception {
        PaymentResponse payment = new PaymentResponse();
        payment.setId(1L);
        payment.setAmount(new BigDecimal("49.99"));
        when(paymentService.getAllPayments()).thenReturn(List.of(payment));

        mockMvc.perform(get("/api/payments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].amount").value(49.99));
    }

    @Test
    void getPaymentById_returnsPaymentDto() throws Exception {
        PaymentResponse payment = new PaymentResponse();
        payment.setId(1L);
        when(paymentService.getPaymentById(1L)).thenReturn(payment);

        mockMvc.perform(get("/api/payments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void createPayment_returnsCreatedAndValidatesRequest() throws Exception {
        PaymentResponse payment = new PaymentResponse();
        payment.setId(1L);
        payment.setAmount(new BigDecimal("49.99"));
        when(paymentService.createPayment(any())).thenReturn(payment);

        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"memberId":1,"membershipId":2,"amount":49.99,"paymentDate":"2025-01-01T12:00:00","paymentMethod":"CASH"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));

        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"memberId":1}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updatePayment_returnsUpdatedDto() throws Exception {
        PaymentResponse payment = new PaymentResponse();
        payment.setId(1L);
        payment.setAmount(new BigDecimal("59.99"));
        when(paymentService.updatePayment(eq(1L), any())).thenReturn(payment);

        mockMvc.perform(put("/api/payments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"amount":59.99}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(59.99));
    }

    @Test
    void deletePayment_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/payments/1"))
                .andExpect(status().isNoContent());

        verify(paymentService).deletePayment(1L);
    }
}
