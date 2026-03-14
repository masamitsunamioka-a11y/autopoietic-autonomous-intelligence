package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.impl;

import com.google.gson.Gson;
import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.Event;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.Events;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class EventsImpl implements Events {
    private static final Logger logger = LoggerFactory.getLogger(EventsImpl.class);
    private static final String POISON = "__CLOSE__";
    private final LinkedBlockingQueue<String> queue =
        new LinkedBlockingQueue<>();
    private final Gson gson = new Gson();

    @Override
    public void queue(Event event) {
        this.queue.offer(this.gson.toJson(event));
    }

    @Override
    public String poll(int timeout, TimeUnit unit) {
        try {
            return this.queue.poll(timeout, unit);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }

    @Override
    public boolean isPoison(String value) {
        return POISON.equals(value);
    }

    @Override
    public void close() {
        this.queue.offer(POISON);
    }
}
