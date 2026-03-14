package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api;

import org.jboss.weld.environment.se.Weld;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.impl.ServerImpl;

import java.util.concurrent.CountDownLatch;

public class EntryPoint {
    private static final Logger logger = LoggerFactory.getLogger(EntryPoint.class);

    public static void main(String[] args) {
        new EntryPoint().initializeAndRun();
    }

    private void initializeAndRun() {
        try (var weld = new Weld()
            .property("org.jboss.weld.se.archive.isolation", "false")
            .initialize()) {
            var server = new ServerImpl(weld);
            var latch = new CountDownLatch(1);
            Runtime.getRuntime().addShutdownHook(
                new Thread(() -> {
                    server.stop();
                    latch.countDown();
                }));
            server.start();
            try {
                latch.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
