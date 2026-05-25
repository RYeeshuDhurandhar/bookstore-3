package edu.cmu.bookstore.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Utility class for working with JSON responses.
 *
 * This class currently provides helper logic for extracting generated
 * text content from Gemini API responses.
 */
public class JsonUtil {

    /**
     * Shared ObjectMapper instance used for JSON parsing.
     */
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private JsonUtil() {
    }

    /**
     * Extracts the generated text from a Gemini API JSON response.
     *
     * The method navigates the expected response structure:
     * candidates[0].content.parts[0].text
     *
     * If the input is null, empty, malformed, or does not contain
     * the expected structure, the method returns null.
     *
     * @param json raw JSON response returned by the Gemini API
     * @return extracted text content, or null if it cannot be found
     */
    public static String extractGeminiText(String json) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }

        try {
            JsonNode root = OBJECT_MAPPER.readTree(json);
            JsonNode candidates = root.path("candidates");

            // Gemini responses are expected to contain at least one candidate.
            if (!candidates.isArray() || candidates.isEmpty()) {
                return null;
            }

            JsonNode firstCandidate = candidates.get(0);
            JsonNode parts = firstCandidate.path("content").path("parts");

            // The generated content is expected to be stored in the first part.
            if (!parts.isArray() || parts.isEmpty()) {
                return null;
            }

            JsonNode firstPart = parts.get(0);
            JsonNode textNode = firstPart.path("text");

            // Return null when the expected text field is missing or null.
            if (textNode.isMissingNode() || textNode.isNull()) {
                return null;
            }

            return textNode.asText();
        } catch (Exception ex) {
            // Return null instead of propagating parsing errors to callers.
            return null;
        }
    }
}