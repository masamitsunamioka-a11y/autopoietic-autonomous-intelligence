package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.impl;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.Exchange;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

public class ExchangeImpl implements Exchange {
    private final Gson gson = new Gson();
    private final HttpExchange raw;

    public ExchangeImpl(HttpExchange raw) {
        this.raw = raw;
    }

    @Override
    public String method() {
        return this.raw.getRequestMethod();
    }

    @Override
    public Exchange header(String key, String value) {
        this.raw.getResponseHeaders()
            .set(key, value);
        return this;
    }

    @Override
    public void send(int status) {
        try {
            var hasContentType = this.raw
                .getResponseHeaders()
                .containsKey("Content-Type");
            this.raw.sendResponseHeaders(
                status, hasContentType ? 0 : -1);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void send(int status, Object body) {
        try {
            if (!this.raw.getResponseHeaders()
                .containsKey("Content-Type")) {
                this.header(
                    "Content-Type",
                    "application/json");
            }
            var bytes = this.gson.toJson(body)
                .getBytes(StandardCharsets.UTF_8);
            this.raw.sendResponseHeaders(
                status, bytes.length);
            try (var out = this.raw.getResponseBody()) {
                out.write(bytes);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public <T> T body(Class<T> type) {
        return this.gson.fromJson(
            new InputStreamReader(
                this.raw.getRequestBody(),
                StandardCharsets.UTF_8),
            type);
    }

    @Override
    public void flush(String text) {
        try {
            var out = this.raw.getResponseBody();
            out.write(
                text.getBytes(StandardCharsets.UTF_8));
            out.flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
