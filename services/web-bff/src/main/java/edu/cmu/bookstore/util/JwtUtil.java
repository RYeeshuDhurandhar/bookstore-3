package edu.cmu.bookstore.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.cmu.bookstore.exception.UnauthorizedException;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Set;

/**
 * Utility component responsible for validating JWT-based authorization data.
 *
 * This class validates:
 * - presence of the Authorization header
 * - correct Bearer token format
 * - basic JWT structure
 * - required payload claims
 * - token expiration
 *
 * This implementation checks the decoded JWT payload and required claims,
 * but does not verify the cryptographic signature.
 */
@Component
public class JwtUtil {

    /**
     * Set of valid subject values allowed in the JWT "sub" claim.
     */
    private static final Set<String> VALID_SUBJECTS =
            Set.of("starlord", "gamora", "drax", "rocket", "groot");

    /**
     * Object mapper used to parse the decoded JWT payload JSON.
     */
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Validates the Authorization header and extracts the bearer token.
     *
     * This method ensures that:
     * - the header is present and not blank
     * - the header begins with "Bearer "
     * - the extracted token is then validated
     *
     * @param authorizationHeader incoming Authorization header value
     * @throws UnauthorizedException if the header is missing or malformed
     */
    public void validateAuthorizationHeader(String authorizationHeader) {
        // Reject missing or blank Authorization headers.
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            throw new UnauthorizedException("Missing Authorization header.");
        }

        // Reject headers that do not follow the Bearer token format.
        if (!authorizationHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException("Invalid Authorization header.");
        }

        // Extract the token value after the "Bearer " prefix.
        String token = authorizationHeader.substring(7).trim();

        // Validate the extracted JWT token.
        validateToken(token);
    }

    /**
     * Validates the structure and payload claims of the JWT token.
     *
     * This method checks:
     * - that the token has exactly three dot-separated parts
     * - that the payload can be Base64 URL-decoded
     * - that the payload is valid JSON
     * - that the "sub" claim exists and has an allowed value
     * - that the "iss" claim exists and equals "cmu.edu"
     * - that the "exp" claim exists and is numeric
     * - that the token has not expired
     *
     * @param token raw JWT token string
     * @throws UnauthorizedException if the token is invalid or expired
     */
    private void validateToken(String token) {
        try {
            // Split the JWT into header, payload, and signature sections.
            String[] parts = token.split("\\.");

            // A valid JWT must contain exactly three sections.
            if (parts.length != 3) {
                throw new UnauthorizedException("Invalid JWT token.");
            }

            // Decode the payload section using Base64 URL decoding.
            String payloadJson = new String(
                    Base64.getUrlDecoder().decode(parts[1]),
                    StandardCharsets.UTF_8
            );

            // Parse the decoded JSON payload.
            JsonNode payload = objectMapper.readTree(payloadJson);

            // Extract the required claims from the JWT payload.
            String sub = payload.path("sub").asText(null);
            String iss = payload.path("iss").asText(null);
            JsonNode expNode = payload.get("exp");

            // Validate the subject claim against the allowed subject list.
            if (sub == null || !VALID_SUBJECTS.contains(sub)) {
                throw new UnauthorizedException("Invalid JWT token.");
            }

            // Validate that the issuer claim matches the expected issuer.
            if (iss == null || !"cmu.edu".equals(iss)) {
                throw new UnauthorizedException("Invalid JWT token.");
            }

            // Validate that the expiration claim exists and is numeric.
            if (expNode == null || !expNode.isNumber()) {
                throw new UnauthorizedException("Invalid JWT token.");
            }

            // Compare the expiration time with the current time in epoch seconds.
            long exp = expNode.asLong();
            long now = Instant.now().getEpochSecond();

            // Reject the token if it has already expired.
            if (exp <= now) {
                throw new UnauthorizedException("JWT token expired.");
            }
        } catch (UnauthorizedException ex) {
            // Re-throw application-specific authorization exceptions unchanged.
            throw ex;
        } catch (Exception ex) {
            // Convert all parsing, decoding, or unexpected failures into a generic invalid token error.
            throw new UnauthorizedException("Invalid JWT token.");
        }
    }
}