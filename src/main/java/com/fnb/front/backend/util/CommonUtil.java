package com.fnb.front.backend.util;

import java.util.Date;

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

    public static String generateOrderId() {
        return "ORDER_" + new Date().getTime();
    }
}
