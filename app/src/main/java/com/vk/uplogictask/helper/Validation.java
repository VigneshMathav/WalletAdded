package com.vk.uplogictask.helper;

import java.util.Calendar;

public class Validation {

    public static boolean isValidCardNumber(String cardNumber) {
        String cardNumberPattern = "^\\d{4}-\\d{4}-\\d{4}-\\d{4}$";
        return cardNumber.matches(cardNumberPattern);
    }

    public static boolean isValidExpiryDate(String expiryDate) {
        String expiryDatePattern = "^(0[1-9]|1[0-2])/\\d{2}$";
        if (!expiryDate.matches(expiryDatePattern)) {
            return false;
        }

        String[] parts = expiryDate.split("/");
        int month = Integer.parseInt(parts[0]);
        int year = Integer.parseInt(parts[1]) + 2000; // Convert YY to YYYY

        Calendar today = Calendar.getInstance();
        int currentMonth = today.get(Calendar.MONTH) + 1; // Months are 0-based
        int currentYear = today.get(Calendar.YEAR);

        return (year > currentYear) || (year == currentYear && month >= currentMonth);
    }
}
