package com.fnb.backend.controller.domain.response;


import lombok.Data;

@Data
public class KaKaoEnrollResponse extends CustomResponse {
    private String tid;
    private String next_redirect_app_url;
    private String next_redirect_mobile_url;
    private String next_redirect_pc_url;
    private String android_app_scheme;
    private String ios_app_scheme;
    private String created_at;

    public static CustomResponse newInstance() {
        return null;
    }
}
