package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/// SSE endpoint: GET /api/events
/// Keeps connection open; writes heartbeat every 30 s.
class SseHandler implements HttpHandler {
    private static final Logger logger = LoggerFactory.getLogger(SseHandler.class);
    private static final String HEARTBEAT = ": heartbeat\n\n";
    private static final int HEARTBEAT_SECONDS = 30;
    private final SseRegistry registry;

    SseHandler(SseRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }
        exchange.getResponseHeaders().set("Content-Type", "text/event-stream");
        exchange.getResponseHeaders().set("Cache-Control", "no-cache");
        exchange.getResponseHeaders().set("Connection", "keep-alive");
        exchange.sendResponseHeaders(200, 0);
        var queue = this.registry.subscribe();
        try (var out = exchange.getResponseBody()) {
            while (true) {
                var payload = queue.poll(HEARTBEAT_SECONDS, TimeUnit.SECONDS);
                if (payload == null) {
                    out.write(HEARTBEAT.getBytes(StandardCharsets.UTF_8));
                    out.flush();
                    continue;
                }
                if (SseRegistry.isPoison(payload)) {
                    break;
                }
                var frame = "data: " + payload + "\n\n";
                out.write(frame.getBytes(StandardCharsets.UTF_8));
                out.flush();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            logger.debug("SSE client disconnected: {}", e.getMessage());
        } finally {
            this.registry.unsubscribe(queue);
        }
    }
}
