package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.Exchange;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.Handler;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.Monitor;

public class MonitorHandler implements Handler {
    private static final Logger logger = LoggerFactory.getLogger(MonitorHandler.class);
    private final Monitor monitor;

    public MonitorHandler(Monitor monitor) {
        this.monitor = monitor;
    }

    @Override
    public void handle(Exchange exchange) {
        if (!"GET".equalsIgnoreCase(exchange.method())) {
            exchange.send(405);
            return;
        }
        exchange.send(200, this.monitor.content());
    }
}
