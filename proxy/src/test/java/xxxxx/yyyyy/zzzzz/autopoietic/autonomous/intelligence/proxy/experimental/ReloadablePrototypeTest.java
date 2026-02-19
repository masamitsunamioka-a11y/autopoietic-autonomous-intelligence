package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy.experimental;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

class ReloadablePrototypeTest {
    /// @Test
    void test() throws InterruptedException {
        ReloadablePrototype container = new ReloadablePrototype();
        AtomicBoolean running = new AtomicBoolean(true);
        try (ExecutorService executor = Executors.newFixedThreadPool(6)) {
            for (int i = 0; i < 5; i++) {
                final int threadNo = i;
                executor.submit(() -> {
                    Thread.currentThread().setName("Seeker-Thread-" + threadNo);
                    while (running.get()) {
                        container.get(null);
                    }
                });
            }
            executor.submit(() -> {
                Thread.currentThread().setName("Shaper-Thread");
                while (running.get()) {
                    try {
                        container.reload();
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            Thread.sleep(60000);
            running.set(false);
            executor.shutdown();
        }
    }
}
