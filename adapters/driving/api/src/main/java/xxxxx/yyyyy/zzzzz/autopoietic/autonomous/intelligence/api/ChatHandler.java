package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.signaling.StimulusImpl;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Receptor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class ChatHandler implements HttpHandler {
    private static final Logger logger = LoggerFactory.getLogger(ChatHandler.class);
    private final Receptor receptor;
    private final SseRegistry sseRegistry;

    public ChatHandler(Receptor receptor,
                       SseRegistry sseRegistry) {
        this.receptor = receptor;
        this.sseRegistry = sseRegistry;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }
        var body = new String(
            exchange.getRequestBody().readAllBytes(),
            StandardCharsets.UTF_8);
        var input = this.extractInput(body);
        if (input == null || input.isBlank()) {
            exchange.sendResponseHeaders(400, -1);
            return;
        }
        try {
            this.receptor.transduce(new StimulusImpl(input));
        } catch (Exception e) {
            logger.error("[UI] respond failed", e);
            this.sseRegistry.broadcast(
                this.sseRegistry.buildJson(
                    "error", "system", e.getMessage()));
        }
        exchange.getResponseHeaders().set(
            "Content-Type", "application/json");
        var ok = "{}".getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, ok.length);
        try (var out = exchange.getResponseBody()) {
            out.write(ok);
        }
    }

    private String extractInput(String json) {
        var map = new Gson().fromJson(json, Map.class);
        var input = map.get("input");
        return input != null ? input.toString() : null;
    }
}
