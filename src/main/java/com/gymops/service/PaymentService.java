package com.gymops.service;

import com.gymops.dto.payment.PaymentCreateRequest;
import com.gymops.dto.payment.PaymentResponse;
import com.gymops.dto.payment.PaymentUpdateRequest;

import java.util.List;

public interface PaymentService {

    PaymentResponse createPayment(PaymentCreateRequest request);

    PaymentResponse getPaymentById(Long id);

    List<PaymentResponse> getAllPayments();

    PaymentResponse updatePayment(Long id, PaymentUpdateRequest request);

    void deletePayment(Long id);
}
