package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api;

import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ArquillianExtension.class)
@RunAsClient
class KandelCircuitIT {
    private static final Logger logger = LoggerFactory.getLogger(KandelCircuitIT.class);
    private static final URI BASE = URI.create("http://127.0.0.1:8080");
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final java.util.concurrent.atomic.AtomicInteger perceptCount =
        new java.util.concurrent.atomic.AtomicInteger(0);

    @Test
    void simple() throws Exception {
        List.of(
            "Hello! What can you do?",
            "Tell me about autopoiesis in one sentence."
        ).forEach(x -> this.stimulate(x, 1));
    }

    @Test
    void complex() throws Exception {
        List.of("""
            Compare GDP-weighted per capita carbon emissions across G7 nations,
            calculate the annual reduction rate needed to meet Paris Agreement 2030 targets,
            and create a priority ranking with specific policy recommendations
            based on each country's energy mix and industrial structure."""
        ).forEach(x -> this.stimulate(x, 15));
    }

    @Test
    void monitor() throws Exception {
        var response = this.httpClient.send(
            HttpRequest.newBuilder()
                .uri(BASE.resolve("/api/monitor"))
                .GET()
                .build(),
            HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertFalse(response.body().isEmpty());
        logger.info("monitor: {}", response.body().substring(0, Math.min(200, response.body().length())));
    }

    private void stimulate(String message, long timeout) {
        try {
            var response = this.chat(message);
            assertEquals(200, response.statusCode());
            var skip = this.perceptCount.get();
            var result = this.awaitPercept(skip)
                .get(timeout, TimeUnit.MINUTES);
            this.perceptCount.incrementAndGet();
            assertTrue(result.contains("percept-generated"));
            logger.info("percept: {}", result.substring(
                0, Math.min(200, result.length())));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private CompletableFuture<String> awaitPercept(int skip) {
        var future = new CompletableFuture<String>();
        this.httpClient.sendAsync(
            HttpRequest.newBuilder()
                .uri(BASE.resolve("/api/events"))
                .GET()
                .build(),
            HttpResponse.BodyHandlers.ofLines()
        ).thenAccept(response -> response.body()
            .filter(line -> line.contains("percept-generated"))
            .skip(skip)
            .findFirst()
            .ifPresent(future::complete));
        return future;
    }

    private HttpResponse<String> chat(String message) throws Exception {
        var escaped = message.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "");
        var json = "{\"payload\":\"" + escaped + "\"}";
        var response = this.httpClient.send(
            HttpRequest.newBuilder()
                .uri(BASE.resolve("/api/chat"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build(),
            HttpResponse.BodyHandlers.ofString());
        logger.info("chat response: {} {}", response.statusCode(), response.body());
        return response;
    }
}
