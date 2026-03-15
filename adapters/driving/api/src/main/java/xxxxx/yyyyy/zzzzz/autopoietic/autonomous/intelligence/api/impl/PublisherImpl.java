package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.impl;

import com.google.gson.Gson;
import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.Event;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.Publisher;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.Subscriber;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

@ApplicationScoped
public class PublisherImpl implements Publisher {
    private static final Logger logger = LoggerFactory.getLogger(PublisherImpl.class);
    private static final String POISON = "__CLOSE__";
    private final List<LinkedBlockingQueue<String>> subscribers =
        new CopyOnWriteArrayList<>();
    private final Gson gson = new Gson();

    @Override
    public void publish(Event event) {
        var json = this.gson.toJson(event);
        for (var queue : this.subscribers) {
            queue.offer(json);
        }
    }

    @Override
    public Subscriber subscribe() {
        var queue = new LinkedBlockingQueue<String>();
        this.subscribers.add(queue);
        return new SubscriberImpl(
            queue, this.subscribers);
    }

    @Override
    public boolean isPoison(String value) {
        return POISON.equals(value);
    }

    @Override
    public void close() {
        for (var queue : this.subscribers) {
            queue.offer(POISON);
        }
    }
}
