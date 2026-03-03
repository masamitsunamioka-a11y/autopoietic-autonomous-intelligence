package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.ui;

import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

/// Manages SSE subscriber queues.
/// Each connected client gets one BlockingQueue<String>.
@ApplicationScoped
class SseRegistry {
    private static final Logger logger = LoggerFactory.getLogger(SseRegistry.class);
    private static final String POISON = "__CLOSE__";
    private final CopyOnWriteArrayList<BlockingQueue<String>> subscribers
        = new CopyOnWriteArrayList<>();

    BlockingQueue<String> subscribe() {
        var queue = new LinkedBlockingQueue<String>();
        this.subscribers.add(queue);
        logger.debug("SSE subscriber added (total={})", this.subscribers.size());
        return queue;
    }

    void unsubscribe(BlockingQueue<String> queue) {
        this.subscribers.remove(queue);
        logger.debug("SSE subscriber removed (total={})", this.subscribers.size());
    }

    void broadcast(String jsonData) {
        for (var queue : this.subscribers) {
            queue.offer(jsonData);
        }
    }

    void closeAll() {
        for (var queue : this.subscribers) {
            queue.offer(POISON);
        }
    }

    static boolean isPoison(String value) {
        return POISON.equals(value);
    }

    static String buildJson(String type, String location, String content) {
        return "{\"type\":\"" + esc(type) + "\","
            + "\"location\":\"" + esc(location) + "\","
            + "\"content\":\"" + esc(content) + "\"}";
    }

    private static String esc(String s) {
        return s.replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t");
    }
}
