package com.fnb.front.backend.controller.domain.pay;

import com.fnb.front.backend.controller.domain.implement.IPay;

import com.fnb.front.backend.controller.domain.request.*;
import com.fnb.front.backend.controller.domain.response.*;
import com.fnb.front.backend.controller.domain.response.KakaoPayCancelResponse;
import com.fnb.front.backend.controller.dto.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Component
public class KakaoPay implements IPay {

    private final String SECRET_KEY = "YOUR_SECRET_KEY"; // Replace with your actual key
    private final String REQUEST_API_URL = "https://open-api.kakaopay.com/online/v1/payment/ready";
    private final String APPROVE_API_URL = "https://open-api.kakaopay.com/online/v1/payment/approve";
    private final String FAIL_API_URL = "https://developers.kakao.com/fail";
    private final String CANCEL_API_URL  = "https://open-api.kakaopay.com/online/v1/payment/cancel";

    @Override
    public RequestPaymentResponse request(RequestPayment requestPayment) {

        HttpHeaders headers = new HttpHeaders();
        RequestPaymentResponse requestPaymentResponse;

        headers.set("Authorization", "SECRET_KEY " + SECRET_KEY);
        headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);

        KakaoPayRequestResultDto requestBody = KakaoPayRequestResultDto.builder()
                .cid(requestPayment.getPaymentKey())
                .partnerOrderId(requestPayment.getOrderId())
                .partnerUserId(requestPayment.getMemberName())
                .itemName(requestPayment.getProductName())
                .quantity(requestPayment.getQuantity())
                .totalAmount(requestPayment.getPurchasePrice())
                .vatAmount(requestPayment.getVatAmount())
                .taxFreeAmount(requestPayment.getTaxAmount())
                .approvalUrl(APPROVE_API_URL + "/" + requestPayment.getAttemptKey())
                .failUrl(FAIL_API_URL)
                .cancelUrl(CANCEL_API_URL)
                .build();

        HttpEntity<KakaoPayRequestResultDto> httpEntity = new HttpEntity<>(requestBody, headers);
        RestTemplate restTemplate = new RestTemplate();

        try {
            KakaoPayRequestResponse response = restTemplate.postForObject(REQUEST_API_URL, httpEntity, KakaoPayRequestResponse.class);
            requestPaymentResponse = RequestPaymentResponse.builder()
                    .redirectUrl(Objects.requireNonNull(response).getNextRedirectMobileUrl())
                    .transactionId(response.getTid()).build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return requestPaymentResponse;
    }

    @Override
    public void pay() {

    }

    @Override
    public ApprovePaymentResponse approve(ApproveRequest kakaoPaymentApproveDto) {

        RestTemplate restTemplate = new RestTemplate();

        KakaoPayApproveRequest requestBody = KakaoPayApproveRequest.builder()
                .cid(kakaoPaymentApproveDto.getPaymentKey())
                .tid(kakaoPaymentApproveDto.getTransactionId())
                .partnerOrderId(kakaoPaymentApproveDto.getOrderId())
                .partnerUserId(kakaoPaymentApproveDto.getMemberName())
                .pgToken(kakaoPaymentApproveDto.getPgToken())
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "SECRET_KEY " + SECRET_KEY);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<KakaoPayApproveRequest> httpEntity = new HttpEntity<>(requestBody, headers);

        try {
            KakaoPayApproveResponse response = restTemplate.postForObject(APPROVE_API_URL, httpEntity, KakaoPayApproveResponse.class);

            return ApprovePaymentResponse.builder()
                    .approvalId(Objects.requireNonNull(response).getAid())
                    .transactionId(response.getTid())
                    .memberName(response.getPartnerUserId())
                    .orderId(response.getPartnerOrderId())
                    .paymentMethod(response.getPaymentMethodType())
                    .productName(response.getItemName())
                    .quantity(response.getQuantity())
                    .totalAmount(response.getAmount().getTotal())
                    .taxFree(response.getAmount().getTaxFree())
                    .vat(response.getAmount().getVat())
                    .approvedAt(LocalDateTime.parse(response.getApprovedAt()))
                    .isFreeInstall(response.getCardInfo().getInterestFreeInstall())
                    .binNumber(response.getCardInfo().getBin())
                    .cardType(response.getCardInfo().getCardType())
                    .install(response.getCardInfo().getInstallMonth())
                    .installType(response.getCardInfo().getInstallmentType())
                    .cardCorp(response.getCardInfo().getKakaopayPurchaseCorp())
                    .cardCorpCode(response.getCardInfo().getKakaopayPurchaseCorpCode())
                    .issuer(response.getCardInfo().getKakaopayIssuerCorp())
                    .issuerCode(response.getCardInfo().getKakaopayIssuerCorpCode())
                    .build();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CancelPaymentResponse cancel(CancelRequest cancelRequest) {
        RestTemplate restTemplate = new RestTemplate();
        KakaoPayCancelRequest requestBody = KakaoPayCancelRequest.builder()
                .cid("kakao")
                .tid(cancelRequest.getTransactionId())
                .cancelAmount(cancelRequest.getCancelAmount())
                .cancelTaxFreeAmount(cancelRequest.getCancelTaxFreeAmount())
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "SECRET_KEY " + SECRET_KEY);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<KakaoPayCancelRequest> httpEntity = new HttpEntity<>(requestBody, headers);

        try {
            KakaoPayCancelResponse response = restTemplate.postForObject(CANCEL_API_URL, httpEntity, KakaoPayCancelResponse.class);

            return CancelPaymentResponse.builder()
                    .cancellationId(Objects.requireNonNull(response).getAid())
                    .transactionId(response.getTid())
                    .orderId(response.getPartnerOrderId())
                    .memberId(response.getPartnerUserId())
                    .paymentMethod(response.getPaymentMethodType())
                    .totalAmount(response.getAmount().getTotal())
                    .taxFree(response.getAmount().getTaxFree())
                    .vat(response.getAmount().getVat())
                    .point(response.getAmount().getPoint())
                    .discount(BigDecimal.valueOf(response.getCancelAmount().getDiscount()))
                    .greenDeposit(BigDecimal.valueOf(response.getCancelAmount().getGreenDeposit()))
                    .build();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
