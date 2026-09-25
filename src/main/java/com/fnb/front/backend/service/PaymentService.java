package com.fnb.front.backend.service;

import com.fnb.front.backend.controller.domain.*;
import com.fnb.front.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;

    public void insertPaymentCancel(PaymentCancel paymentCancel) {
        this.paymentRepository.insertPaymentCancel(paymentCancel);
    }

    public void insertPaymentElement(PaymentElement paymentElement) {
        this.paymentRepository.insertPaymentElement(paymentElement);
    }

    public int insertPayment(Payment payment) {
        return this.paymentRepository.insertPayment(payment);
    }

    public void insertPaymentAttempt(PaymentAttempt paymentAttempt) {
        this.paymentRepository.insertPaymentAttempt(paymentAttempt);
    }

    public Payment findPayment(String orderId) {
        return this.paymentRepository.findPayment(orderId);
    }

    public PaymentAttempt findPaymentAttempt(String attemptKey) {
        return this.paymentRepository.findPaymentAttempt(attemptKey);
    }

    public PaymentAttempt findOrderPaymentAttempt(String orderId, String attemptKey) {
        return this.paymentRepository.findOrderPaymentAttempt(orderId, attemptKey);
    }

    public Payment findPayment(Integer paymentId) {
        return this.paymentRepository.findPayment(paymentId);
    }

    public PaymentElement findPaymentElement(String orderId) {
        return this.paymentRepository.findPaymentElement(orderId);
    }

    public int updateAttemptStatus(String attemptKey, String expectedStatus, String updateStatus) {
        return this.paymentRepository.updateAttemptStatus(attemptKey, expectedStatus, updateStatus);
    }

    public int updatePaymentStatus(Integer paymentId, String expectedStatus, String updateStatus) {
        return this.paymentRepository.updatePaymentStatus(paymentId, expectedStatus, updateStatus);
    }
}
