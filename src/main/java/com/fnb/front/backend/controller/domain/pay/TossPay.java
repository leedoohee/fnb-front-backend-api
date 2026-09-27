package com.fnb.front.backend.controller.domain.pay;

import com.fnb.front.backend.controller.domain.implement.IPay;
import com.fnb.front.backend.controller.domain.request.*;
import com.fnb.front.backend.controller.domain.response.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriUtils;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

@Component
public class TossPay implements IPay {

    private static final String APPROVE_API_URL =
            "https://api.tosspayments.com/v1/payments/confirm";
    private static final String CANCEL_API_URL =
            "https://api.tosspayments.com/v1/payments/%s/cancel";
    private static final String DEFAULT_CANCEL_REASON = "고객 요청에 의한 취소";

    @Value("${toss.payment.secret-key}")
    private String secretKey;

    @Override
    public RequestPaymentResponse request(RequestPayment requestPayment) {
        return null;
    }

    @Override
    public void pay() {
    }

    @Override
    public ApprovePaymentResponse approve(ApproveRequest approveRequest) {

        TossPayApproveRequest requestBody = TossPayApproveRequest.builder()
                .paymentKey(approveRequest.getPaymentKey())
                .orderId(approveRequest.getOrderId())
                .amount(approveRequest.getAmount())
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(secretKey, "");
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<TossPayApproveRequest> httpEntity = new HttpEntity<>(requestBody, headers);
        RestTemplate restTemplate = new RestTemplate();

        try {
            TossPayResponse response = restTemplate.postForObject(APPROVE_API_URL, httpEntity, TossPayResponse.class);
            TossPayResponse.Card card = response.getCard();

            ApprovePaymentResponse.ApprovePaymentResponseBuilder builder = ApprovePaymentResponse.builder()
                            .approvalId(response.getLastTransactionKey())
                            .transactionId(response.getPaymentKey())
                            .orderId(response.getOrderId())
                            .paymentMethod(response.getMethod())
                            .productName(response.getOrderName())
                            .totalAmount(response.getTotalAmount())
                            .taxFree(zeroIfNull(response.getTaxFreeAmount()))
                            .vat(zeroIfNull(response.getVat()))
                            .approvedAt(parseDateTime(response.getApprovedAt()));
                            if (card != null) {
                                builder.cardType(card.getCardType())
                                        .install(card.getInstallmentPlanMonths() == null
                                                ? null
                                                : String.valueOf(card.getInstallmentPlanMonths()))
                                        .isFreeInstall(card.getIsInterestFree() == null
                                                ? null
                                                : String.valueOf(card.getIsInterestFree()))
                                        .cardCorpCode(card.getAcquirerCode())
                                        .issuerCode(card.getIssuerCode());
                            }

            return builder.build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CancelPaymentResponse cancel(CancelRequest cancelRequest) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(secretKey, "");
        headers.setContentType(MediaType.APPLICATION_JSON);

        String cancelReason = StringUtils.hasText(cancelRequest.getCancelReason())
                ? cancelRequest.getCancelReason()
                : DEFAULT_CANCEL_REASON;

        TossPayCancelRequest requestBody = TossPayCancelRequest.builder()
                .cancelReason(cancelReason)
                .cancelAmount(cancelRequest.getCancelAmount())
                .taxFreeAmount(cancelRequest.getCancelTaxFreeAmount())
                .build();

        String encodedPaymentKey = UriUtils.encodePathSegment(
                cancelRequest.getTransactionId(),
                StandardCharsets.UTF_8
        );

        TossPayResponse response = restTemplate.postForObject(
                CANCEL_API_URL.formatted(encodedPaymentKey),
                new HttpEntity<>(requestBody, headers),
                TossPayResponse.class
        );

        TossPayResponse.Cancel latestCancel = this.getLatestCancel(Objects.requireNonNull(response).getCancels());

        return CancelPaymentResponse.builder()
                .cancellationId(latestCancel.getTransactionKey())
                .transactionId(response.getPaymentKey())
                .orderId(response.getOrderId())
                .paymentMethod(response.getMethod())
                .totalAmount(latestCancel.getCancelAmount())
                .taxFree(zeroIfNull(latestCancel.getTaxFreeAmount()))
                .vat(zeroIfNull(cancelRequest.getCancelVatAmount()))
                .point(BigDecimal.ZERO)
                .discount(zeroIfNull(latestCancel.getEasyPayDiscountAmount()))
                .greenDeposit(BigDecimal.ZERO)
                .build();
    }

    private LocalDateTime parseDateTime(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }

        return OffsetDateTime.parse(value).toLocalDateTime();
    }

    private BigDecimal zeroIfNull(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private TossPayResponse.Cancel getLatestCancel(List<TossPayResponse.Cancel> cancels) {
        if (cancels == null || cancels.isEmpty()) {
            throw new IllegalStateException("토스 취소 응답에 취소 이력이 없습니다.");
        }

        return cancels.get(cancels.size() - 1);
    }
}