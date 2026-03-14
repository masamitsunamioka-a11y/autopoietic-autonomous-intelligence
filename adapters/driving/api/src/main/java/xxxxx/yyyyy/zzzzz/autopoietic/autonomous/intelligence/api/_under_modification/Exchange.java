package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api._under_modification;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class Exchange {
    private final Gson gson = new Gson();
    private final HttpExchange raw;

    public Exchange(HttpExchange raw) {
        this.raw = raw;
    }

    public String method() {
        return this.raw.getRequestMethod();
    }

    public Exchange header(String key, String value) {
        this.raw.getResponseHeaders().set(key, value);
        return this;
    }

    public void send(int status) throws IOException {
        var streaming = this.raw.getResponseHeaders().containsKey("Content-Type");
        this.raw.sendResponseHeaders(status, streaming ? 0 : -1);
    }

    public void send(int status, Object body) throws IOException {
        if (!this.raw.getResponseHeaders().containsKey("Content-Type")) {
            this.header("Content-Type", "application/json");
        }
        var bytes = this.gson.toJson(body).getBytes(StandardCharsets.UTF_8);
        this.raw.sendResponseHeaders(status, bytes.length);
        try (var out = this.raw.getResponseBody()) {
            out.write(bytes);
        }
    }

    public <T> T body(Class<T> type) {
        return this.gson.fromJson(
            new InputStreamReader(this.raw.getRequestBody(), StandardCharsets.UTF_8), type);
    }

    public void flush(String text) throws IOException {
        var out = this.raw.getResponseBody();
        out.write(text.getBytes(StandardCharsets.UTF_8));
        out.flush();
    }
}
