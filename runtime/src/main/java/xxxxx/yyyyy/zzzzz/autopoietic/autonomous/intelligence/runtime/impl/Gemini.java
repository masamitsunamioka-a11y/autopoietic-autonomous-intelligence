package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.impl;

import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.ExternalService;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.JsonParser;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped
public class Gemini implements ExternalService<String> {
    private static final Logger logger = LoggerFactory.getLogger(Gemini.class);
    private static final String API_KEY = System.getenv("GEMINI_API_KEY");
    private static final String API_BASE_URL = "https://generativelanguage.googleapis.com";
    private static final String API_VERSION = "v1";
    private static final String MODEL_NAME = "gemini-2.0-flash";
    private static final String API_URL = String.format("%s/%s/models/%s:generateContent?key=%s",
            API_BASE_URL, API_VERSION, MODEL_NAME, API_KEY);
    /// private static final int MAX_RETRIES = 3;
    /// private static final long INITIAL_BACKOFF_MS = 2000L;
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final JsonParser jsonParser;

    public Gemini(JsonParser jsonParser) {
        this.jsonParser = jsonParser;
    }

    @Override
    public String call(String text) {
        if (API_KEY.isEmpty()) {
            throw new IllegalStateException("Environment variable 'GEMINI_API_KEY' is not set.");
        }
        GeminiRequest request = new GeminiRequest(
                List.of(new Content(List.of(new Part(text)))),
                Map.of("temperature", 0.7)
        );
        String payload = this.jsonParser.to(request);
        return this.executeWithRetry(payload, 0);
    }

    private String executeWithRetry(String payload, int retryCount) {
        try {
            logger.trace("[GEMINI_REQUEST] Payload: {}", payload);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload, StandardCharsets.UTF_8))
                    .build();
            HttpResponse<String> response = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body();
            int status = response.statusCode();
            logger.trace("[GEMINI_RESPONSE] Status: {}, Body: {}", status, body);
            ///if (status == 429 && retryCount < MAX_RETRIES) {
            ///    long backoff = INITIAL_BACKOFF_MS * (long) Math.pow(2, retryCount);
            ///    logger.warn("PureJavaIntelligence API 429 received. Retrying in {}ms... ({}/{})",
            ///            backoff,
            ///            retryCount + 1,
            ///            MAX_RETRIES);
            ///    Thread.sleep(backoff);
            ///    return this.executeWithRetry(payload, retryCount + 1);
            ///}
            if (status != 200) {
                throw new RuntimeException("PureJavaIntelligence API Error: " + status + " - Body: " + body);
            }
            GeminiResponse responseObj = this.jsonParser.from(body, GeminiResponse.class);
            return Optional.ofNullable(responseObj.text())
                    .orElseThrow(() -> new RuntimeException(
                            "Response text extraction failed. Raw Body: " + body));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    private record GeminiRequest(List<Content> contents,
                                 Map<String, Object> generationConfig) {
    }

    private record GeminiResponse(List<Candidate> candidates) {
        String text() {
            return Optional.ofNullable(this.candidates)
                    .filter(x -> !x.isEmpty())
                    .map(x -> x.getFirst().content())
                    .map(Content::parts)
                    .filter(x -> !x.isEmpty())
                    .map(x -> x.getFirst().text())
                    .orElse(null);
        }
    }

    private record Candidate(Content content) {
    }

    private record Content(List<Part> parts) {
    }

    private record Part(String text) {
    }
}
