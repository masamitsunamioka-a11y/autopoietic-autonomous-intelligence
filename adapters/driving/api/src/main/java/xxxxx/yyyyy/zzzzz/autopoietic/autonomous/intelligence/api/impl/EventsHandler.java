package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.Exchange;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.Handler;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.Publisher;

import java.io.UncheckedIOException;
import java.util.concurrent.TimeUnit;

public class EventsHandler implements Handler {
    private static final Logger logger = LoggerFactory.getLogger(EventsHandler.class);
    private static final String HEARTBEAT = ": heartbeat\n\n";
    private static final int HEARTBEAT_SECONDS = 30;
    private final Publisher publisher;

    public EventsHandler(Publisher publisher) {
        this.publisher = publisher;
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
        try (var subscriber = this.publisher.subscribe()) {
            while (!Thread.currentThread().isInterrupted()) {
                var event = subscriber.poll(
                    HEARTBEAT_SECONDS, TimeUnit.SECONDS);
                if (event == null) {
                    exchange.flush(HEARTBEAT);
                    continue;
                }
                if (this.publisher.isPoison(event)) {
                    break;
                }
                exchange.flush(
                    "data: " + event + "\n\n");
            }
        } catch (UncheckedIOException e) {
            logger.debug("SSE client disconnected");
        }
    }
}
