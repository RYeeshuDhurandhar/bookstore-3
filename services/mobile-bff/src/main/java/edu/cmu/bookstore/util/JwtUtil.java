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
 * - required JWT payload claims
 * - token expiration
 *
 * For this implementation, validation focuses on decoding the JWT payload
 * and checking required claims rather than verifying the token signature.
 */
@Component
public class JwtUtil {

    /**
     * Set of valid subject values allowed in the JWT "sub" claim.
     */
    private static final Set<String> VALID_SUBJECTS =
            Set.of("starlord", "gamora", "drax", "rocket", "groot");

    /**
     * Object mapper used to parse decoded JWT payload JSON.
     */
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Validates the Authorization header and extracts the bearer token.
     *
     * This method ensures that:
     * - the header is present and not blank
     * - the header begins with "Bearer "
     * - the extracted token is passed to JWT validation
     *
     * @param authorizationHeader incoming Authorization header value
     * @throws UnauthorizedException if the header is missing or malformed
     */
    public void validateAuthorizationHeader(String authorizationHeader) {
        // Reject missing or blank Authorization headers.
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            throw new UnauthorizedException("Missing Authorization header.");
        }

        // Reject headers that do not use the Bearer token format.
        if (!authorizationHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException("Invalid Authorization header.");
        }

        // Extract the token portion after the "Bearer " prefix.
        String token = authorizationHeader.substring(7).trim();

        // Validate the extracted JWT token.
        validateToken(token);
    }

    /**
     * Validates the structure and payload claims of a JWT token.
     *
     * This method checks:
     * - the token has exactly three dot-separated parts
     * - the payload can be Base64 URL-decoded
     * - the payload is valid JSON
     * - the "sub" claim is present and allowed
     * - the "iss" claim is present and equals "cmu.edu"
     * - the "exp" claim exists and is numeric
     * - the token is not expired
     *
     * Any parsing or validation failure results in an UnauthorizedException.
     *
     * @param token raw JWT token string
     * @throws UnauthorizedException if the token is invalid or expired
     */
    private void validateToken(String token) {
        try {
            // Split the JWT into header, payload, and signature parts.
            String[] parts = token.split("\\.");

            // A valid JWT must contain exactly three parts.
            if (parts.length != 3) {
                throw new UnauthorizedException("Invalid JWT token.");
            }

            // Decode the payload section of the JWT using Base64 URL decoding.
            String payloadJson = new String(
                    Base64.getUrlDecoder().decode(parts[1]),
                    StandardCharsets.UTF_8
            );

            // Parse the decoded payload JSON.
            JsonNode payload = objectMapper.readTree(payloadJson);

            // Read the required claims from the payload.
            String sub = payload.path("sub").asText(null);
            String iss = payload.path("iss").asText(null);
            JsonNode expNode = payload.get("exp");

            // Validate that the subject exists and is one of the allowed users.
            if (sub == null || !VALID_SUBJECTS.contains(sub)) {
                throw new UnauthorizedException("Invalid JWT token.");
            }

            // Validate that the issuer exists and matches the expected issuer.
            if (iss == null || !"cmu.edu".equals(iss)) {
                throw new UnauthorizedException("Invalid JWT token.");
            }

            // Validate that the expiration claim exists and is numeric.
            if (expNode == null || !expNode.isNumber()) {
                throw new UnauthorizedException("Invalid JWT token.");
            }

            // Read the expiration time and compare it with the current epoch time.
            long exp = expNode.asLong();
            long now = Instant.now().getEpochSecond();

            // Reject the token if it has already expired.
            if (exp <= now) {
                throw new UnauthorizedException("JWT token expired.");
            }
        } catch (UnauthorizedException ex) {
            // Re-throw application-specific authorization errors unchanged.
            throw ex;
        } catch (Exception ex) {
            // Convert all other parsing or decoding failures into a generic invalid token error.
            throw new UnauthorizedException("Invalid JWT token.");
        }
    }
}