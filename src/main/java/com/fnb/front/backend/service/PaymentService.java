package com.fnb.front.backend.service;

import com.fnb.front.backend.controller.domain.*;
import com.fnb.front.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;

    public int insertPaymentCancel(PaymentCancel paymentCancel) {
        return this.paymentRepository.insertPaymentCancel(paymentCancel);
    }

    public void insertPaymentElement(PaymentElement paymentElement) {
        this.paymentRepository.insertPaymentElement(paymentElement);
    }

    public int insertPayment(Payment payment) {
        return this.paymentRepository.insertPayment(payment);
    }

    public Payment findPayment(String orderId) {
        return this.paymentRepository.findPayment(orderId);
    }

    public Payment findPayment(Integer paymentId) {
        return this.paymentRepository.findPayment(paymentId);
    }

    public PaymentElement findPaymentElement(String orderId) {
        return this.paymentRepository.findPaymentElement(orderId);
    }
}
