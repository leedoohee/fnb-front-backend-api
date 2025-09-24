package com.fnb.front.backend.controller.domain.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KakaoPayRequestResponse {
    private String tid;

    @JsonProperty("next_redirect_app_url")
    private String nextRedirectAppUrl;

    @JsonProperty("next_redirect_mobile_url")
    private String nextRedirectMobileUrl;

    @JsonProperty("next_redirect_pc_url")
    private String nextRedirectPcUrl;

    @JsonProperty("android_app_scheme")
    private String androidAppScheme;

    @JsonProperty("ios_app_scheme")
    private String iosAppScheme;

    @JsonProperty("created_at")
    private String createdAt;
}