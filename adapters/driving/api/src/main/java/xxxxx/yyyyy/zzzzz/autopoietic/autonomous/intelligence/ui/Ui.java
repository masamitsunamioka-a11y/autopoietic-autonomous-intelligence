package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.ui;

import com.sun.net.httpserver.HttpServer;
import org.jboss.weld.environment.se.Weld;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.cognitive.Cortex;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.homeostatic.Drive;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.homeostatic.Salience;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Thalamus;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Transducer;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.working.Episode;

import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

public class Ui {
    private static final Logger logger = LoggerFactory.getLogger(Ui.class);
    private static final int PORT = 8080;

    public static void main(String[] args) throws Exception {
        var container = new Weld()
            .property("org.jboss.weld.se.archive.isolation", "false")
            .initialize();
        var registry = container.select(SseRegistry.class).get();
        var transducer = container.select(Transducer.class).get();
        var thalamus = container.select(Thalamus.class).get();
        var cortex = container.select(Cortex.class).get();
        var salience = container.select(Salience.class).get();
        var episode = container.select(Episode.class).get();
        /// Force @PostConstruct by resolving the client proxy via no-op toString()
        container.select(Drive.class).get().toString();
        var server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.setExecutor(Executors.newCachedThreadPool());
        server.createContext("/api/events", new SseHandler(registry));
        server.createContext("/api/chat",
            new ChatHandler(
                registry, transducer, thalamus,
                cortex, salience, episode));
        server.createContext("/", new ResourceHandler());
        var latch = new CountDownLatch(1);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutting down AAI UI…");
            registry.closeAll();
            server.stop(2);
            container.close();
            latch.countDown();
        }));
        server.start();
        logger.info("AAI UI listening on http://localhost:{}", PORT);
        latch.await();
    }
}
