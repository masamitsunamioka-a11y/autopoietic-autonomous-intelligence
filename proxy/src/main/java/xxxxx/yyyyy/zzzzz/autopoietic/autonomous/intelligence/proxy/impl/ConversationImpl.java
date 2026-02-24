package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy.impl;

import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy.Conversation;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@ApplicationScoped
public class ConversationImpl implements Conversation {
    private static final Logger logger = LoggerFactory.getLogger(ConversationImpl.class);
    private final AtomicReference<String> id;

    public ConversationImpl() {
        this.id = new AtomicReference<>();
    }

    @Override
    public void begin() {
        this.begin(UUID.randomUUID().toString());
    }

    @Override
    public void begin(String id) {
        if (!this.id.compareAndSet(null, id)) {
            throw new IllegalStateException();
        }
    }

    @Override
    public void end() {
        if (Objects.isNull(this.id.getAndSet(null))) {
            throw new IllegalStateException();
        }
    }

    @Override
    public boolean isTransient() {
        return Objects.isNull(this.id.get());
    }
}
