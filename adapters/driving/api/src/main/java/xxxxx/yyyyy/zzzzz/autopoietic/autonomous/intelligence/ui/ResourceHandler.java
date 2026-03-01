package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.ui;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/// Serves static frontend assets from the classpath.
/// Routes:
///   GET /assets/* → assets/<filename> (JS, CSS, source maps)
///   GET *         → index.html (SPA fallback)
class ResourceHandler implements HttpHandler {
    private static final Logger logger = LoggerFactory.getLogger(ResourceHandler.class);

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }
        var path = exchange.getRequestURI().getPath();
        if (path.startsWith("/assets/")) {
            this.serveAsset(exchange, path);
        } else {
            this.serveIndex(exchange);
        }
    }

    private void serveIndex(HttpExchange exchange) throws IOException {
        try (var in = ResourceHandler.class.getClassLoader()
            .getResourceAsStream("index.html")) {
            if (in == null) {
                logger.error("index.html not found in classpath");
                exchange.sendResponseHeaders(404, -1);
                return;
            }
            var body = in.readAllBytes();
            exchange.getResponseHeaders()
                .set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, body.length);
            try (var out = exchange.getResponseBody()) {
                out.write(body);
            }
        }
    }

    private void serveAsset(HttpExchange exchange, String path) throws IOException {
        /// Strip leading slash: "/assets/index-abc.js" → "assets/index-abc.js"
        var resource = path.substring(1);
        try (var in = ResourceHandler.class.getClassLoader()
            .getResourceAsStream(resource)) {
            if (in == null) {
                logger.warn("Asset not found in classpath: {}", resource);
                exchange.sendResponseHeaders(404, -1);
                return;
            }
            var body = in.readAllBytes();
            exchange.getResponseHeaders().set("Content-Type", mimeType(resource));
            exchange.sendResponseHeaders(200, body.length);
            try (var out = exchange.getResponseBody()) {
                out.write(body);
            }
        }
    }

    /// Derive MIME type from file extension.
    private static String mimeType(String path) {
        if (path.endsWith(".js")) return "application/javascript; charset=UTF-8";
        if (path.endsWith(".css")) return "text/css; charset=UTF-8";
        if (path.endsWith(".map")) return "application/json; charset=UTF-8";
        if (path.endsWith(".svg")) return "image/svg+xml";
        if (path.endsWith(".png")) return "image/png";
        return "application/octet-stream";
    }
}
