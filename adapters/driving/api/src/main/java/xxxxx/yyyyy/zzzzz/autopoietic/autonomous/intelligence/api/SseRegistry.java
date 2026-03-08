package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api;

import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

@ApplicationScoped
public class SseRegistry {
    private static final Logger logger = LoggerFactory.getLogger(SseRegistry.class);
    private static final String POISON = "__CLOSE__";
    private final CopyOnWriteArrayList<BlockingQueue<String>> subscribers
        = new CopyOnWriteArrayList<>();

    public BlockingQueue<String> subscribe() {
        var queue = new LinkedBlockingQueue<String>();
        this.subscribers.add(queue);
        logger.debug("SSE subscriber added (total={})", this.subscribers.size());
        return queue;
    }

    public void unsubscribe(BlockingQueue<String> queue) {
        this.subscribers.remove(queue);
        logger.debug("SSE subscriber removed (total={})", this.subscribers.size());
    }

    public void broadcast(String jsonData) {
        for (var queue : this.subscribers) {
            queue.offer(jsonData);
        }
    }

    public void closeAll() {
        for (var queue : this.subscribers) {
            queue.offer(POISON);
        }
    }

    public boolean isPoison(String value) {
        return POISON.equals(value);
    }

    public String buildJson(String type, String location, String content) {
        return "{\"type\":\"" + this.esc(type) + "\","
            + "\"location\":\"" + this.esc(location) + "\","
            + "\"content\":\"" + this.esc(content) + "\"}";
    }

    private String esc(String s) {
        return s.replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t");
    }
}
