package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api._under_modification.Exchange;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class EventsHandler implements HttpHandler {
    private static final Logger logger = LoggerFactory.getLogger(EventsHandler.class);
    private static final String HEARTBEAT = ": heartbeat\n\n";
    private static final int HEARTBEAT_SECONDS = 30;
    private final Events events;

    public EventsHandler(Events events) {
        this.events = events;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        var exchange = new Exchange(httpExchange);
        if (!"GET".equalsIgnoreCase(exchange.method())) {
            exchange.send(405);
            return;
        }
        exchange
            .header("Content-Type", "text/event-stream")
            .header("Cache-Control", "no-cache")
            .header("Connection", "keep-alive")
            .send(200);
        try {
            while (true) {
                var event = this.events.poll(HEARTBEAT_SECONDS, TimeUnit.SECONDS);
                if (event == null) {
                    exchange.flush(HEARTBEAT);
                    continue;
                }
                if (this.events.isPoison(event)) {
                    break;
                }
                exchange.flush("data: " + event + "\n\n");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
