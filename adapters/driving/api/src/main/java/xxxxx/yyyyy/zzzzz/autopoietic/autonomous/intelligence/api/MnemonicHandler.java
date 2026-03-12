package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class MnemonicHandler implements HttpHandler {
    private static final Logger logger = LoggerFactory.getLogger(MnemonicHandler.class);
    private final MnemonicWatcher mnemonicWatcher;

    public MnemonicHandler(MnemonicWatcher mnemonicWatcher) {
        this.mnemonicWatcher = mnemonicWatcher;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }
        var json = this.mnemonicWatcher.mnemonicJson()
            .getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set(
            "Content-Type", "application/json");
        exchange.sendResponseHeaders(200, json.length);
        try (var out = exchange.getResponseBody()) {
            out.write(json);
        }
    }
}
