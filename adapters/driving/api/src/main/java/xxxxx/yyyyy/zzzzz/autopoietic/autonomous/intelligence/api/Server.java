package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api;

import com.sun.net.httpserver.HttpServer;
import org.jboss.weld.environment.se.Weld;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.mnemonic.Episode;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.modulatory.Default;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.modulatory.Salience;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Thalamus;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Transducer;

import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

public class Server {
    private static final Logger logger = LoggerFactory.getLogger(Server.class);
    private static final int PORT = 8080;

    public static void main(String[] args) throws Exception {
        try (var container = new Weld()
            .property("org.jboss.weld.se.archive.isolation", "false")
            .initialize()) {
            var salience = container.select(Salience.class).get();
            var thalamus = container.select(Thalamus.class).get();
            var transducer = container.select(Transducer.class).get();
            var episode = container.select(Episode.class).get();
            /// Force @PostConstruct by resolving the client proxy via no-op toString()
            container.select(Default.class).get().toString();
            var watcher = container.select(NeuralWatcher.class).get();
            watcher.toString();
            var registry = container.select(SseRegistry.class).get();
            var server = HttpServer.create(new InetSocketAddress(PORT), 0);
            server.setExecutor(Executors.newCachedThreadPool());
            server.createContext("/api/events",
                new SseHandler(registry));
            server.createContext("/api/chat",
                new ChatHandler(salience, thalamus, transducer, episode, registry));
            server.createContext("/api/neural",
                new NeuralHandler(watcher));
            var latch = new CountDownLatch(1);
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                logger.info("Shutting down AAI API…");
                registry.closeAll();
                server.stop(2);
                latch.countDown();
            }));
            server.start();
            logger.info("AAI API listening on http://localhost:{}", PORT);
            latch.await();
        }
    }
}
