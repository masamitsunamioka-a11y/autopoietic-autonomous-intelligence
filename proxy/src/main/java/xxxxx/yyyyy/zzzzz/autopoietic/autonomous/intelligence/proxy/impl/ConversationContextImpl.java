package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.ConversationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy.Context;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy.Contextual;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy.Conversation;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class ConversationContextImpl implements Context, Conversation {
    private static final Logger logger = LoggerFactory.getLogger(ConversationContextImpl.class);
    private final ThreadLocal<Map<Contextual<?>, Object>> instances;

    public ConversationContextImpl() {
        this.instances = new InheritableThreadLocal<>();
    }

    @Override
    public Class<? extends Annotation> scope() {
        return ConversationScoped.class;
    }

    @Override
    public <T> T get(Contextual<T> contextual) {
        /// this.lock.readLock().lock();
        try {
            return this.resolve(contextual);
        } finally {
            /// this.lock.readLock().unlock();
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T resolve(Contextual<T> contextual) {
        var instances = this.instances.get();
        if (Objects.isNull(instances)) {
            throw new IllegalStateException();
        }
        return (T) instances.computeIfAbsent(
            contextual, x -> contextual.create(null));
    }

    @Override
    public void begin() {
        this.begin(UUID.randomUUID().toString());
    }

    @Override
    public void begin(String id) {
        if (!this.isTransient()) {
            throw new IllegalStateException();
        }
        this.instances.set(new ConcurrentHashMap<>());
    }

    @Override
    public void end() {
        if (this.isTransient()) {
            throw new IllegalStateException();
        }
        this.instances.remove();
    }

    @Override
    public boolean isTransient() {
        return this.instances.get() == null;
    }
}
