package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

import static xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Util.isEmpty;

public class GeminiClient {
    private static final Logger logger = LoggerFactory.getLogger(GeminiClient.class);
    private static final String API_KEY = System.getenv("GEMINI_API_KEY");
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + API_KEY;
    private static final int MAX_RETRIES = 3;
    private static final long INITIAL_BACKOFF_MS = 2000L;
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public String post(String text) {
        if (isEmpty(API_KEY)) {
            throw new IllegalStateException("Environment variable 'GEMINI_API_KEY' is not set.");
        }
        GeminiRequest request = new GeminiRequest(
                List.of(new Content(List.of(new Part(text)))),
                Map.of("temperature", 0.7)
        );
        String payload = this.gson.toJson(request);
        return this.executeWithRetry(payload, 0);
    }

    private String executeWithRetry(String jsonPayload, int retryCount) {
        try {
            logger.trace("[GEMINI_REQUEST] Payload: {}", jsonPayload);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload, StandardCharsets.UTF_8))
                    .build();
            HttpResponse<String> response = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body();
            int status = response.statusCode();
            logger.trace("[GEMINI_RESPONSE] Status: {}, Body: {}", status, body);
            if (status == 429 && retryCount < MAX_RETRIES) {
                long backoff = INITIAL_BACKOFF_MS * (long) Math.pow(2, retryCount);
                logger.warn("GeminiIntelligence API 429 received. Retrying in {}ms... ({}/{})",
                        backoff,
                        retryCount + 1,
                        MAX_RETRIES);
                Thread.sleep(backoff);
                return this.executeWithRetry(jsonPayload, retryCount + 1);
            }
            if (status != 200) {
                throw new RuntimeException("GeminiIntelligence API Error: " + status + " - Body: " + body);
            }
            GeminiResponse responseObj = this.gson.fromJson(body, GeminiResponse.class);
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
