package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.impl;

import com.sun.net.httpserver.HttpServer;
import org.jboss.weld.environment.se.WeldContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.Monitor;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.Publisher;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.Server;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.homeostatic.Sleep;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.networking.Default;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Receptor;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;

import static java.util.concurrent.Executors.newCachedThreadPool;

public class ServerImpl implements Server {
    private static final Logger logger = LoggerFactory.getLogger(ServerImpl.class);
    private static final int PORT = 8080;
    private final HttpServer httpServer;
    private final Publisher publisher;

    public ServerImpl(WeldContainer weld) {
        var receptor =
            weld.select(Receptor.class).get();
        var monitor1 =
            weld.select(Monitor.class, Neural.Literal.INSTANCE).get();
        var monitor2 =
            weld.select(Monitor.class, Mnemonic.Literal.INSTANCE).get();

        /// Force @PostConstruct by resolving the client proxy
        weld.select(Default.class).get().toString();
        weld.select(Sleep.class).get().toString();
        monitor1.toString();
        monitor2.toString();

        this.publisher =
            weld.select(Publisher.class).get();

        try {
            this.httpServer = HttpServer.create(
                new InetSocketAddress(PORT), 0);
            this.httpServer.setExecutor(
                newCachedThreadPool());

            this.httpServer.createContext(
                "/api/events",
                x -> new EventsHandler(this.publisher)
                    .handle(new ExchangeImpl(x)));

            this.httpServer.createContext(
                "/api/chat",
                x -> new ChatHandler(receptor, this.publisher)
                    .handle(new ExchangeImpl(x)));

            this.httpServer.createContext(
                "/api/monitor/neural",
                x -> new MonitorHandler(monitor1)
                    .handle(new ExchangeImpl(x)));

            this.httpServer.createContext(
                "/api/monitor/mnemonic",
                x -> new MonitorHandler(monitor2)
                    .handle(new ExchangeImpl(x)));

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void start() {
        this.httpServer.start();
    }

    @Override
    public void stop() {
        this.publisher.close();
        this.httpServer.stop(2);
    }
}
