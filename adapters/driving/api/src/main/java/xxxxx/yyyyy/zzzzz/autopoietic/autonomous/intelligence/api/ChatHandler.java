package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api._under_modification.Exchange;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.signaling.StimulusImpl;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Receptor;

import java.io.IOException;
import java.util.Map;

public class ChatHandler implements HttpHandler {
    private static final Logger logger = LoggerFactory.getLogger(ChatHandler.class);
    private final Receptor receptor;
    private final Events events;

    private record Input(String payload) {
        boolean isBlank() {
            return this.payload == null || this.payload.isBlank();
        }
    }

    public ChatHandler(Receptor receptor, Events events) {
        this.receptor = receptor;
        this.events = events;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        var exchange = new Exchange(httpExchange);
        if (!"POST".equalsIgnoreCase(exchange.method())) {
            exchange.send(405);
            return;
        }
        var input = exchange.body(Input.class);
        if (input == null || input.isBlank()) {
            exchange.send(400);
            return;
        }
        try {
            this.receptor.transduce(new StimulusImpl(input.payload()));
        } catch (Exception e) {
            this.events.queue(new Event(
                "error",
                "system",
                e.getMessage()));
        }
        exchange.send(200, Map.of());
    }
}
