package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api;

import com.sun.net.httpserver.HttpServer;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.homeostatic.Sleep;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.networking.Default;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Receptor;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;

import static java.util.concurrent.Executors.newCachedThreadPool;

public class Server {
    private static final Logger logger = LoggerFactory.getLogger(Server.class);
    private static final int PORT = 8080;
    private final Neural neural = Neural.Literal.INSTANCE;
    private final Mnemonic mnemonic = Mnemonic.Literal.INSTANCE;

    public static void main(String[] args) {
        new Server().initializeAndRun();
    }

    private void initializeAndRun() {
        try (var weld = new Weld()
            .property("org.jboss.weld.se.archive.isolation", "false")
            .initialize()) {
            var server = this.createServer(weld);
            var events = weld.select(Events.class).get();
            var latch = new CountDownLatch(1);
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                events.close();
                server.stop(2);
                latch.countDown();
            }));
            server.start();
            try {
                latch.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        ;
    }

    private HttpServer createServer(WeldContainer weld) {
        var receptor = weld.select(Receptor.class).get();
        var monitor1 = weld.select(Monitor.class, this.neural).get();
        var monitor2 = weld.select(Monitor.class, this.mnemonic).get();
        /// Force @PostConstruct by resolving the client proxy via no-op toString()
        weld.select(Default.class).get().toString();
        weld.select(Sleep.class).get().toString();
        monitor1.toString();
        monitor2.toString();
        var events = weld.select(Events.class).get();
        try {
            var server = HttpServer.create(new InetSocketAddress(PORT), 0);
            server.setExecutor(newCachedThreadPool());
            server.createContext("/api/events",
                new EventsHandler(events));
            server.createContext("/api/chat",
                new ChatHandler(receptor, events));
            server.createContext("/api/monitor/neural",
                new MonitorHandler(monitor1));
            server.createContext("/api/monitor/mnemonic",
                new MonitorHandler(monitor2));
            return server;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
