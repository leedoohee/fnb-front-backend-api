package com.fnb.front.backend.controller.domain.pay;

import com.fnb.front.backend.controller.domain.implement.IPay;
import com.fnb.front.backend.controller.domain.request.ApproveRequest;
import com.fnb.front.backend.controller.domain.request.CancelRequest;
import com.fnb.front.backend.controller.domain.request.RequestPayment;
import com.fnb.front.backend.controller.domain.response.ApprovePaymentResponse;
import com.fnb.front.backend.controller.domain.response.CancelPaymentResponse;
import com.fnb.front.backend.controller.domain.response.NaverPayResponse;
import com.fnb.front.backend.controller.domain.response.RequestPaymentResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Component
public class NaverPay implements IPay {

    private static final String SUCCESS = "Success";
    private static final String CANCEL_NOT_COMPLETE = "CancelNotComplete";
    private static final String ALREADY_CANCELED = "AlreadyCanceled";
    private static final String APPROVAL_SUCCESS = "SUCCESS";
    private static final String DEFAULT_CANCEL_REASON = "고객 요청에 의한 취소";
    private static final String MERCHANT_CANCEL_REQUESTER = "2";
    private static final DateTimeFormatter NAVER_DATE_TIME =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    @Value("${naver.payment.client-id}")
    private String clientId;
    @Value("${naver.payment.client-secret}")
    private String clientSecret;
    @Value("${naver.payment.chain-id}")
    private String chainId;
    @Value("${naver.payment.mode}")
    private String mode;
    @Value("${naver.payment.return-url}")
    private String returnUrl;
    @Value("${naver.payment.apply-url}")
    private String applyApiUrl;
    @Value("${naver.payment.cancel-url}")
    private String cancelApiUrl;

    @Override
    public RequestPaymentResponse request(RequestPayment requestPayment) {
        return RequestPaymentResponse.builder()
                .clientId(this.clientId)
                .chainId(this.chainId)
                .mode(this.mode)
                .returnUrl(appendPath(this.returnUrl, requestPayment.getAttemptKey()))
                .build();
    }

    @Override
    public void pay() {
    }

    @Override
    public ApprovePaymentResponse approve(ApproveRequest approveRequest) {
        RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("paymentId", approveRequest.getTransactionId());

        NaverPayResponse response = restTemplate.postForObject(
                applyApiUrl,
                new HttpEntity<>(form, createHeaders(approveRequest.getIdempotencyKey())),
                NaverPayResponse.class
        );

        NaverPayResponse.Detail detail = requireApprovalDetail(Objects.requireNonNull(response));
        return toApprovePaymentResponse(detail, approveRequest);
    }

    @Override
    public CancelPaymentResponse cancel(CancelRequest cancelRequest) {
        RestTemplate restTemplate = new RestTemplate();
        BigDecimal cancelAmount = Objects.requireNonNull(cancelRequest.getCancelAmount(), "cancelAmount는 필수입니다.");
        BigDecimal taxExScopeAmount = zeroIfNull(cancelRequest.getCancelTaxFreeAmount());
        BigDecimal taxScopeAmount = cancelAmount.subtract(taxExScopeAmount);

        if (taxScopeAmount.signum() < 0) {
            throw new IllegalArgumentException("비과세 취소 금액은 전체 취소 금액보다 클 수 없습니다.");
        }

        String cancelReason = StringUtils.hasText(cancelRequest.getCancelReason())
                ? cancelRequest.getCancelReason()
                : DEFAULT_CANCEL_REASON;

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("paymentId", cancelRequest.getTransactionId());
        form.add("merchantPayTransactionKey", cancelRequest.getOrderId());
        form.add("cancelAmount", cancelAmount.toPlainString());
        form.add("cancelReason", cancelReason);
        form.add("cancelRequester", MERCHANT_CANCEL_REQUESTER);
        form.add("taxScopeAmount", taxScopeAmount.toPlainString());
        form.add("taxExScopeAmount", taxExScopeAmount.toPlainString());
        form.add("doCompareRest", "1");
        form.add("expectedRestAmount", zeroIfNull(cancelRequest.getExpectedRestAmount()).toPlainString());

        NaverPayResponse response = restTemplate.postForObject(cancelApiUrl,
                new HttpEntity<>(form, createHeaders(cancelRequest.getIdempotencyKey())),
                NaverPayResponse.class
        );

        return toCancelPaymentResponse(requireCancelResponse(Objects.requireNonNull(response)), cancelRequest);
    }

    private HttpHeaders createHeaders(String idempotencyKey) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("X-Naver-Client-Id", clientId);
        headers.set("X-Naver-Client-Secret", clientSecret);
        headers.set("X-NaverPay-Chain-Id", chainId);
        headers.set("X-NaverPay-Idempotency-Key", idempotencyKey);
        return headers;
    }

    private NaverPayResponse.Detail requireApprovalDetail(NaverPayResponse response) {

        if (response.getBody() == null || response.getBody().getDetail() == null) {
            throw new IllegalStateException("네이버페이 승인 응답에 결제 상세가 없습니다.");
        }

        NaverPayResponse.Detail detail = response.getBody().getDetail();

        if (!APPROVAL_SUCCESS.equals(detail.getAdmissionState())) {
            throw new IllegalStateException("네이버페이 승인 상태가 SUCCESS가 아닙니다.");
        }

        if (!StringUtils.hasText(detail.getPaymentId())) {
            detail.setPaymentId(response.getBody().getPaymentId());
        }

        Objects.requireNonNull(detail.getTotalPayAmount(), "승인 응답 totalPayAmount가 없습니다.");
        return detail;
    }

    private NaverPayResponse.Body requireCancelResponse(NaverPayResponse response) {
        return response.getBody() == null ? new NaverPayResponse.Body() : response.getBody();
    }

    private ApprovePaymentResponse toApprovePaymentResponse(NaverPayResponse.Detail detail, ApproveRequest approveRequest) {
        return ApprovePaymentResponse.builder()
                .approvalId(detail.getPayHistId())
                .transactionId(detail.getPaymentId())
                .memberName(detail.getMerchantUserKey())
                .orderId(detail.getMerchantPayKey())
                .paymentMethod(resolvePaymentMethod(detail.getPrimaryPayMeans(), detail.getNpointPayAmount()))
                .productName(detail.getProductName())
                .totalAmount(detail.getTotalPayAmount())
                .taxFree(zeroIfNull(detail.getTaxExScopeAmount()))
                .vat(zeroIfNull(approveRequest.getVatAmount()))
                .approvedAt(parseDateTime(detail.getAdmissionYmdt()))
                .install(detail.getCardInstCount() == null
                        ? null
                        : String.valueOf(detail.getCardInstCount()))
                .cardCorpCode(detail.getCardCorpCode())
                .build();
    }

    private CancelPaymentResponse toCancelPaymentResponse(NaverPayResponse.Body body, CancelRequest cancelRequest) {
        return CancelPaymentResponse.builder()
                .cancellationId(body.getPayHistId())
                .transactionId(StringUtils.hasText(body.getPaymentId())
                        ? body.getPaymentId()
                        : cancelRequest.getTransactionId())
                .orderId(cancelRequest.getOrderId())
                .paymentMethod(body.getPrimaryPayMeans())
                .totalAmount(cancelRequest.getCancelAmount())
                .taxFree(body.getTaxExScopeAmount() == null
                        ? zeroIfNull(cancelRequest.getCancelTaxFreeAmount())
                        : body.getTaxExScopeAmount())
                .vat(zeroIfNull(cancelRequest.getCancelVatAmount()))
                .point(zeroIfNull(body.getNpointCancelAmount()))
                .discount(zeroIfNull(body.getDiscountCancelAmount()))
                .greenDeposit(zeroIfNull(body.getEnvironmentDepositAmount()))
                .build();
    }

    private String resolvePaymentMethod(String primaryPayMeans, BigDecimal npointPayAmount) {
        if (StringUtils.hasText(primaryPayMeans)) {
            return primaryPayMeans;
        }
        return zeroIfNull(npointPayAmount).signum() > 0 ? "NAVER_POINT" : "NAVER_PAY";
    }

    private LocalDateTime parseDateTime(String value) {
        return StringUtils.hasText(value)
                ? LocalDateTime.parse(value, NAVER_DATE_TIME)
                : null;
    }

    private String appendPath(String baseUrl, String value) {
        return baseUrl.endsWith("/") ? baseUrl + value : baseUrl + "/" + value;
    }

    private BigDecimal zeroIfNull(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

}
