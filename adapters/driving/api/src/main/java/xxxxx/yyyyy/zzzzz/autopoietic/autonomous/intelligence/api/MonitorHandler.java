package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api._under_modification.Exchange;

import java.io.IOException;

public class MonitorHandler implements HttpHandler {
    private static final Logger logger = LoggerFactory.getLogger(MonitorHandler.class);
    private final Monitor monitor;

    public MonitorHandler(Monitor monitor) {
        this.monitor = monitor;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        var exchange = new Exchange(httpExchange);
        if (!"GET".equalsIgnoreCase(exchange.method())) {
            exchange.send(405);
            return;
        }
        exchange.send(200, this.monitor.content());
    }
}
