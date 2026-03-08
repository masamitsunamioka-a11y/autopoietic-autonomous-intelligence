package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class NeuralHandler implements HttpHandler {
    private static final Logger logger = LoggerFactory.getLogger(NeuralHandler.class);
    private final NeuralWatcher neuralWatcher;

    public NeuralHandler(NeuralWatcher neuralWatcher) {
        this.neuralWatcher = neuralWatcher;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }
        var json = this.neuralWatcher.treeJson()
            .getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set(
            "Content-Type", "application/json");
        exchange.sendResponseHeaders(200, json.length);
        try (var out = exchange.getResponseBody()) {
            out.write(json);
        }
    }
}
