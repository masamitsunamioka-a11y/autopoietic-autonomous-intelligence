package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.impl;

import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.Subscriber;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class SubscriberImpl implements Subscriber {
    private final LinkedBlockingQueue<String> queue;
    private final List<LinkedBlockingQueue<String>> subscribers;

    public SubscriberImpl(LinkedBlockingQueue<String> queue,
                          List<LinkedBlockingQueue<String>> subscribers) {

        this.queue = queue;
        this.subscribers = subscribers;
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
    public void close() {
        this.subscribers.remove(this.queue);
    }
}
