package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.homeostatic.Salience;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.mnemonic.Episode;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Thalamus;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Transducer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/// POST /api/chat — receives user input, runs the perception pipeline,
/// and broadcasts the percept via SSE.
class ChatHandler implements HttpHandler {
    private static final Logger logger = LoggerFactory.getLogger(ChatHandler.class);
    private final SseRegistry registry;
    private final Transducer transducer;
    private final Thalamus thalamus;
    private final Salience salience;
    private final Episode episode;

    ChatHandler(
        SseRegistry registry,
        Transducer transducer,
        Thalamus thalamus,
        Salience salience,
        Episode episode) {
        this.registry = registry;
        this.transducer = transducer;
        this.thalamus = thalamus;
        this.salience = salience;
        this.episode = episode;
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
        var input = extractInput(body);
        if (input == null || input.isBlank()) {
            exchange.sendResponseHeaders(400, -1);
            return;
        }
        this.episode.encode(new TraceImpl("user", input));
        this.salience.orient();
        this.registry.broadcast(
            SseRegistry.buildJson("user", "user", input));
        try {
            this.thalamus.relay(
                this.transducer.transduce(
                    new StimulusImpl(input)));
            this.salience.await();
        } catch (Exception e) {
            logger.error("[UI] respond failed", e);
            this.registry.broadcast(
                SseRegistry.buildJson(
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

    /// Minimal JSON parse: extracts the "input" field value.
    private static String extractInput(String json) {
        var key = "\"input\"";
        var idx = json.indexOf(key);
        if (idx < 0) return null;
        var colon = json.indexOf(':', idx + key.length());
        if (colon < 0) return null;
        var quote = json.indexOf('"', colon + 1);
        if (quote < 0) return null;
        var end = quote + 1;
        var sb = new StringBuilder();
        while (end < json.length()) {
            var c = json.charAt(end++);
            if (c == '"') break;
            if (c == '\\' && end < json.length())
                c = json.charAt(end++);
            sb.append(c);
        }
        return sb.toString();
    }
}
