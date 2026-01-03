package com.avarice.helpers;

import java.util.Map;

public class RomanUtil {

    private static final Map<Character, Integer> MAP = Map.of(
            'I', 1,
            'V', 5,
            'X', 10,
            'L', 50,
            'C', 100
    );

    public static int fromRoman(String roman) {
        int result = 0;
        int prev = 0;

        roman = roman.toUpperCase();

        for (int i = roman.length() - 1; i >= 0; i--) {
            int val = MAP.getOrDefault(roman.charAt(i), 0);
            if (val < prev) result -= val;
            else {
                result += val;
                prev = val;
            }
        }
        return result;
    }
}
