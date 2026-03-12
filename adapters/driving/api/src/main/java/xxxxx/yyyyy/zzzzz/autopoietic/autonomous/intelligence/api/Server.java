package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api;

import com.sun.net.httpserver.HttpServer;
import org.jboss.weld.environment.se.Weld;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.homeostatic.Sleep;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.modulatory.Default;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Receptor;

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
            var receptor = container.select(Receptor.class).get();
            /// Force @PostConstruct by resolving the client proxy via no-op toString()
            container.select(Default.class).get().toString();
            container.select(Sleep.class).get().toString();
            var watcher = container.select(NeuralWatcher.class).get();
            watcher.toString();
            var mnemonicWatcher = container.select(MnemonicWatcher.class).get();
            mnemonicWatcher.toString();
            var sseRegistry = container.select(SseRegistry.class).get();
            var server = HttpServer.create(new InetSocketAddress(PORT), 0);
            server.setExecutor(Executors.newCachedThreadPool());
            server.createContext("/api/events",
                new SseHandler(sseRegistry));
            server.createContext("/api/chat",
                new ChatHandler(receptor, sseRegistry));
            server.createContext("/api/neural",
                new NeuralHandler(watcher));
            server.createContext("/api/mnemonic",
                new MnemonicHandler(mnemonicWatcher));
            var latch = new CountDownLatch(1);
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                logger.info("Shutting down AAI API…");
                sseRegistry.closeAll();
                server.stop(2);
                latch.countDown();
            }));
            server.start();
            logger.info("AAI API listening on http://localhost:{}", PORT);
            latch.await();
        }
    }
}
