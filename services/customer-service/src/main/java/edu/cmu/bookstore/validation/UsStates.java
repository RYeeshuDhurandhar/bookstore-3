package edu.cmu.bookstore.validation;

import java.util.Set;

/**
 * Utility class containing valid two-letter US state abbreviations.
 *
 * This class provides a lookup method for validating whether a given
 * state code is a recognized US state or district abbreviation.
 */
public class UsStates {

    /**
     * Set of valid two-letter US state and district abbreviations.
     */
    private static final Set<String> VALID_STATES = Set.of(
            "AL", "AK", "AZ", "AR", "CA", "CO", "CT", "DE", "FL", "GA",
            "HI", "ID", "IL", "IN", "IA", "KS", "KY", "LA", "ME", "MD",
            "MA", "MI", "MN", "MS", "MO", "MT", "NE", "NV", "NH", "NJ",
            "NM", "NY", "NC", "ND", "OH", "OK", "OR", "PA", "RI", "SC",
            "SD", "TN", "TX", "UT", "VT", "VA", "WA", "WV", "WI", "WY",
            "DC"
    );

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private UsStates() {
    }

    /**
     * Checks whether the provided state code is a valid two-letter
     * US state or district abbreviation.
     *
     * The input is normalized by trimming whitespace and converting
     * to uppercase before validation.
     *
     * @param state state code to validate
     * @return true if the state code is valid, otherwise false
     */
    public static boolean isValid(String state) {
        if (state == null) {
            return false;
        }

        // Normalize input before lookup so lowercase and padded values are accepted.
        return VALID_STATES.contains(state.trim().toUpperCase());
    }
}