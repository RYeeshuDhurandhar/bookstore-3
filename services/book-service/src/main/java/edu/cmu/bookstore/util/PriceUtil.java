package edu.cmu.bookstore.util;

import java.math.BigDecimal;

/**
 * Utility class for price-related helper methods.
 *
 * This class currently provides validation support for checking
 * whether a monetary value conforms to the expected decimal precision.
 */
public class PriceUtil {

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private PriceUtil() {
    }

    /**
     * Checks whether the given monetary value has at most two decimal places.
     *
     * Trailing zeros are removed before checking the scale so that values
     * such as 10.0 and 10.00 are treated as valid two-decimal-place amounts.
     *
     * @param value price value to validate
     * @return true if the value is non-null and has at most two decimal places;
     *         false otherwise
     */
    public static boolean hasAtMostTwoDecimalPlaces(BigDecimal value) {
        if (value == null) {
            return false;
        }

        // Normalize the value before checking scale so trailing zeros do not affect validation.
        BigDecimal normalized = value.stripTrailingZeros();
        return normalized.scale() <= 2;
    }
}