package com.fnb.front.backend.controller.domain;

import com.fnb.front.backend.util.CommonUtil;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Merchant {

    @Id
    private int id;
    private String name;
    private String address;
    private String phone;
    private String email;
    private String startLiveTime;
    private String endLiveTime;

    public boolean isLive() throws Exception {
        return CommonUtil.isLiveTime(this.startLiveTime, this.endLiveTime);
    }
}
