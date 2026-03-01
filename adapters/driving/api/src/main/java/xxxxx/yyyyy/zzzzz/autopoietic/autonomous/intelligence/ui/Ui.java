package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.ui;

import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy.Conversation;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy.impl.ClassScannerImpl;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy.impl.ProxyContainerImpl;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.cognitive.Cortex;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.homeostatic.Drive;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.homeostatic.Salience;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Thalamus;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Transducer;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.working.Episode;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

public class Ui {
    private static final Logger logger = LoggerFactory.getLogger(Ui.class);
    private static final int PORT = 8080;

    public static void main(String[] args) throws Exception {
        var registry = new SseRegistry();
        System.setOut(new SsePrintStream(registry, System.out));
        var classScanner = new ClassScannerImpl(
            List.of("xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence"),
            List.of("xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.effectors"));
        var container = new ProxyContainerImpl(classScanner);
        Transducer transducer = container.get(Transducer.class);
        Thalamus thalamus = container.get(Thalamus.class);
        Cortex cortex = container.get(Cortex.class);
        Drive drive = container.get(Drive.class);
        Salience salience = container.get(Salience.class);
        Conversation conversation = container.get(Conversation.class);
        Episode episode = container.get(Episode.class);
        var server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.setExecutor(Executors.newCachedThreadPool());
        server.createContext("/api/events", new SseHandler(registry));
        server.createContext("/api/chat",
            new ChatHandler(registry, transducer, thalamus, cortex, salience, episode));
        server.createContext("/", new ResourceHandler());
        var latch = new CountDownLatch(1);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutting down AAI UI…");
            drive.deactivate();
            conversation.end();
            registry.closeAll();
            server.stop(2);
            latch.countDown();
        }));
        conversation.begin();
        drive.activate();
        server.start();
        logger.info("AAI UI listening on http://localhost:{}", PORT);
        latch.await();
    }
}
