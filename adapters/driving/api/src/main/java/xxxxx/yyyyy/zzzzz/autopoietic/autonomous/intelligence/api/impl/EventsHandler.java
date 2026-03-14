package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.Events;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.Exchange;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.Handler;

import java.util.concurrent.TimeUnit;

public class EventsHandler implements Handler {
    private static final Logger logger = LoggerFactory.getLogger(EventsHandler.class);
    private static final String HEARTBEAT = ": heartbeat\n\n";
    private static final int HEARTBEAT_SECONDS = 30;
    private final Events events;

    public EventsHandler(Events events) {
        this.events = events;
    }

    @Override
    public void handle(Exchange exchange) {
        if (!"GET".equalsIgnoreCase(exchange.method())) {
            exchange.send(405);
            return;
        }
        exchange
            .header("Content-Type", "text/event-stream")
            .header("Cache-Control", "no-cache")
            .header("Connection", "keep-alive")
            .send(200);
        while (!Thread.currentThread().isInterrupted()) {
            var event = this.events.poll(
                HEARTBEAT_SECONDS, TimeUnit.SECONDS);
            if (event == null) {
                exchange.flush(HEARTBEAT);
                continue;
            }
            if (this.events.isPoison(event)) {
                break;
            }
            exchange.flush(
                "data: " + event + "\n\n");
        }
    }
}
