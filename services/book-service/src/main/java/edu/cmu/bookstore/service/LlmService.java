package edu.cmu.bookstore.service;

import edu.cmu.bookstore.model.Book;
import edu.cmu.bookstore.util.JsonUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Service responsible for generating book summaries using a language model.
 *
 * This class attempts to call the Gemini API when an API key is available.
 * If the API key is missing or the external call fails for any reason,
 * it falls back to a locally generated summary so that the application
 * can still function correctly.
 */
@Service
public class LlmService {

    /**
     * API key used to authenticate requests to the LLM provider.
     */
    @Value("${app.llm.api.key:}")
    private String apiKey;

    /**
     * Model name used for summary generation.
     */
    @Value("${app.llm.model:gemini-1.5-flash}")
    private String model;

    /**
     * Configured timeout in milliseconds for LLM-related HTTP calls.
     */
    @Value("${app.llm.timeout.ms:15000}")
    private int timeoutMs;

    /**
     * HTTP client used to send requests to the external LLM API.
     */
    private final RestTemplate restTemplate;

    /**
     * Constructs the service with the required HTTP client dependency.
     *
     * @param restTemplate HTTP client used for external API calls
     */
    public LlmService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Generates a summary for the given book.
     *
     * If no API key is configured, or if the Gemini API call fails,
     * the method returns a fallback summary built locally.
     *
     * @param book book for which the summary should be generated
     * @return generated summary text
     */
    public String generateSummary(Book book) {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            return buildFallbackSummary(book);
        }

        try {
            return callGemini(book);
        } catch (Exception ex) {
            return buildFallbackSummary(book);
        }
    }

    /**
     * Calls the Gemini API to generate a summary for the given book.
     *
     * This method builds the request payload, sends it to the configured
     * Gemini model endpoint, and extracts the generated text from the
     * JSON response.
     *
     * @param book book for which the summary should be generated
     * @return generated summary text, or a fallback summary if the API
     *         response does not contain usable text
     */
    private String callGemini(Book book) {
        String endpoint = "https://generativelanguage.googleapis.com/v1beta/models/"
                + model
                + ":generateContent?key="
                + apiKey;

        String prompt = buildPrompt(book);

        Map<String, Object> textPart = new LinkedHashMap<>();
        textPart.put("text", prompt);

        Map<String, Object> content = new LinkedHashMap<>();
        content.put("parts", List.of(textPart));

        Map<String, Object> requestBody = new LinkedHashMap<>();
        requestBody.put("contents", List.of(content));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                endpoint,
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        String responseBody = response.getBody();
        String extracted = JsonUtil.extractGeminiText(responseBody);

        // Fall back when the external API returns no usable summary text.
        if (extracted == null || extracted.trim().isEmpty()) {
            return buildFallbackSummary(book);
        }

        return extracted.trim();
    }

    /**
     * Builds the prompt sent to the language model.
     *
     * The prompt includes the book metadata and formatting requirements
     * so that the returned summary is clear, coherent, and close to
     * the requested length.
     *
     * @param book book whose fields are used to construct the prompt
     * @return formatted prompt text
     */
    private String buildPrompt(Book book) {
        return """
                Write a clear book summary of about 500 words.

                Title: %s
                Author: %s
                Description: %s
                Genre: %s

                Requirements:
                - Make it coherent and readable.
                - Do not use bullet points.
                - Keep it close to 500 words.
                - Write plain text only.
                """.formatted(
                safe(book.getTitle()),
                safe(book.getAuthor()),
                safe(book.getDescription()),
                safe(book.getGenre())
        );
    }

    /**
     * Builds a deterministic fallback summary when the external LLM
     * cannot be used.
     *
     * This ensures that the application can still return a meaningful
     * summary even without external model access.
     *
     * @param book book whose fields are used to construct the fallback summary
     * @return locally generated fallback summary
     */
    private String buildFallbackSummary(Book book) {
        return """
                %s by %s is a %s book centered around the following premise: %s

                This title can be understood as a work that introduces readers to its subject in an accessible and structured way. Based on the available description, the book appears to focus on its main ideas with enough detail to make the topic understandable to readers who may be approaching it for the first time, while still offering value to more experienced readers. The writing can be seen as organized around the core themes suggested by the description, helping the reader move from the general motivation of the topic toward more specific insights and examples.

                The book’s overall character is shaped by both its subject and genre. Readers can expect a treatment that stays connected to the central description provided for the work and develops it into a fuller narrative or explanation. In that sense, the book likely offers a broader context for why the topic matters, what key concepts define it, and how those concepts connect together. Rather than functioning as a loose collection of ideas, it is better understood as a focused presentation of a central theme.

                Another useful way to think about the book is in terms of audience. It seems likely to appeal to readers who want a concise but meaningful entry point into the topic, as well as those who appreciate a structured overview. The description suggests that the author presents the material in a way that emphasizes clarity and continuity, making the work suitable for readers who want both understanding and direction.

                Overall, %s stands out as a book whose main strength lies in how it develops its stated subject into a readable and informative whole. Through its theme, style, and organization, it offers readers a sustained exploration of the topic introduced in the description, making it a useful and engaging read for those interested in the area.
                """.formatted(
                safe(book.getTitle()),
                safe(book.getAuthor()),
                safe(book.getGenre()),
                safe(book.getDescription()),
                safe(book.getTitle())
        ).replaceAll("\\s+\n", "\n").trim();
    }

    /**
     * Safely normalizes a string value for prompt and fallback generation.
     *
     * Null values are converted to an empty string, and non-null values
     * are trimmed to remove leading and trailing whitespace.
     *
     * @param value input string value
     * @return normalized string value
     */
    private String safe(String value) {
        return value == null ? "" : value.trim();
    }
}