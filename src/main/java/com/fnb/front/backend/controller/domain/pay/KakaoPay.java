package com.fnb.front.backend.controller.domain.pay;

import com.fnb.front.backend.controller.domain.implement.IPay;

import com.fnb.front.backend.controller.domain.response.ApprovePaymentResponse;
import com.fnb.front.backend.controller.domain.response.KakaoPayApproveResponse;
import com.fnb.front.backend.controller.domain.response.KakaoPayRequestResponse;
import com.fnb.front.backend.controller.domain.response.RequestPaymentResponse;
import com.fnb.front.backend.controller.domain.request.RequestPayment;
import com.fnb.front.backend.controller.dto.ApprovePaymentDto;
import com.fnb.front.backend.controller.dto.CancelPaymentDto;
import com.fnb.front.backend.controller.dto.KakaoPayApproveDto;
import com.fnb.front.backend.controller.dto.KakaoPayRequestDto;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Objects;

public class KakaoPay implements IPay {

    private final String SECRET_KEY = "YOUR_SECRET_KEY"; // Replace with your actual key
    private final String REQUEST_API_URL = "https://open-api.kakaopay.com/online/v1/payment/ready";
    private final String APPROVE_API_URL = "https://open-api.kakaopay.com/online/v1/payment/approve";

    @Override
    public RequestPaymentResponse request(RequestPayment requestPayment) {

        HttpHeaders headers = new HttpHeaders();
        RequestPaymentResponse requestPaymentResponse = null;

        headers.set("Authorization", "SECRET_KEY " + SECRET_KEY);
        headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);

        KakaoPayRequestDto requestBody = KakaoPayRequestDto.builder()
                .cid(requestPayment.getPaymentKey())
                .partnerOrderId(requestPayment.getOrderId())
                .partnerUserId(requestPayment.getMemberName())
                .itemName(requestPayment.getProductName())
                .quantity(requestPayment.getQuantity())
                .totalAmount(requestPayment.getPurchasePrice())
                .vatAmount(requestPayment.getVatAmount())
                .taxFreeAmount(requestPayment.getTaxAmount())
                .approvalUrl("https://developers.kakao.com/success")
                .failUrl("https://developers.kakao.com/fail")
                .cancelUrl("https://developers.kakao.com/cancel")
                .build();

        HttpEntity<KakaoPayRequestDto> httpEntity = new HttpEntity<>(requestBody, headers);
        RestTemplate restTemplate = new RestTemplate();

        try {
            KakaoPayRequestResponse response = restTemplate.postForObject(REQUEST_API_URL, httpEntity, KakaoPayRequestResponse.class);
            requestPaymentResponse = RequestPaymentResponse.builder().redirectUrl(Objects.requireNonNull(response).getNextRedirectMobileUrl()).build();
        } catch (Exception e) {
            return null;
        }

        return requestPaymentResponse;
    }

    @Override
    public void pay() {

    }

    @Override
    public ApprovePaymentResponse approve(ApprovePaymentDto approvePaymentDto) {

        RestTemplate restTemplate = new RestTemplate();
        ApprovePaymentResponse approvePaymentResponse = null;

        KakaoPayApproveDto requestBody = KakaoPayApproveDto.builder()
                .cid(approvePaymentDto.getPaymentKey())
                .tid(approvePaymentDto.getTransactionId())
                .partnerOrderId(approvePaymentDto.getOrderId())
                .partnerUserId(approvePaymentDto.getMemberName())
                .pgToken(approvePaymentDto.getPgToken())
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "SECRET_KEY " + SECRET_KEY);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<KakaoPayApproveDto> httpEntity = new HttpEntity<>(requestBody, headers);

        try {
            KakaoPayApproveResponse response = restTemplate.postForObject(APPROVE_API_URL, httpEntity, KakaoPayApproveResponse.class);

            approvePaymentResponse = ApprovePaymentResponse.builder()
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
            return null;
        }

        return approvePaymentResponse;
    }

    @Override
    public void cancel(CancelPaymentDto cancelPaymentDto) {

    }
}
