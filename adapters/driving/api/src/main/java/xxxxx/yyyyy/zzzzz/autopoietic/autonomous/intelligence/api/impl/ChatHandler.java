package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.Exchange;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.Handler;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.Publisher;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.signaling.StimulusImpl;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Receptor;

import java.util.Map;

public class ChatHandler implements Handler {
    private static final Logger logger = LoggerFactory.getLogger(ChatHandler.class);
    private final Receptor receptor;
    private final Publisher publisher;

    private record Input(String payload) {
        boolean isBlank() {
            return this.payload == null
                || this.payload.isBlank();
        }
    }

    public ChatHandler(Receptor receptor, Publisher publisher) {
        this.receptor = receptor;
        this.publisher = publisher;
    }

    @Override
    public void handle(Exchange exchange) {
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
            this.receptor.transduce(
                new StimulusImpl(input.payload()));
        } catch (Exception e) {
            this.publisher.publish(
                new ErrorEvent(e.getMessage()));
        }
        exchange.send(200, Map.of());
    }
}
