package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.sse.Sse;
import jakarta.ws.rs.sse.SseEventSink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.Event;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.Subscriber;

import java.util.concurrent.Flow;

public class SubscriberImpl implements Subscriber {
    private static final Logger logger = LoggerFactory.getLogger(SubscriberImpl.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final SseEventSink sink;
    private final Sse sse;

    public SubscriberImpl(SseEventSink sink, Sse sse) {
        this.sink = sink;
        this.sse = sse;
    }

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        subscription.request(Long.MAX_VALUE);
    }

    @Override
    public void onNext(Event event) {
        if (this.sink.isClosed()) {
            return;
        }
        this.sink.send(this.sse.newEventBuilder()
            .data(this.toJson(event))
            .build());
    }

    @Override
    public void onError(Throwable throwable) {
        logger.warn("subscriber error", throwable);
    }

    @Override
    public void onComplete() {
        if (!this.sink.isClosed()) {
            this.sink.close();
        }
    }

    private String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
