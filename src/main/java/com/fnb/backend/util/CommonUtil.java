package com.fnb.backend.util;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class CommonUtil {

    public static boolean isMinAndMaxBetween(int min, int max, int amount) {
        return min <= amount && max >= amount;
    }

    public static boolean isProductAmountPolicyType(String type) {
        return type.equals("PRODUCT");
    }

    public static boolean isPaymentAmountPolicyType(String type) {
        return type.equals("PAYMENT");
    }

    public static boolean isLiveTime(String startTime, String endTime) throws Exception {
        boolean result = false;

        int openHH = Integer.parseInt(startTime.split(":")[0]);
        int openMM = Integer.parseInt(startTime.split(":")[1]);

        int endHH = Integer.parseInt(endTime.split(":")[0]);
        int endMM = Integer.parseInt(endTime.split(":")[1]);

        LocalDateTime currentTime = LocalDateTime.now();

        LocalTime start  = LocalTime.of(openHH, openMM);
        LocalTime end    = LocalTime.of(endHH, endMM);

        if (end.isBefore(start)) {
            currentTime = currentTime.plusDays(1);
        }

        LocalDateTime startDateTime = currentTime.with(start);
        LocalDateTime endDateTime = currentTime.with(end);

        if (currentTime.isBefore(endDateTime) || currentTime.isAfter(startDateTime)) {
            result = true;
        }

        return result;
    }
}
