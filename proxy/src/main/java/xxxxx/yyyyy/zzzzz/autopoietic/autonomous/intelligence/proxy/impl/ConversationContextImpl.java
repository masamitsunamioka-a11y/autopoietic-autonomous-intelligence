package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy.impl;

import jakarta.enterprise.context.ConversationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy.Context;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy.Contextual;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy.Conversation;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;

public class ConversationContextImpl implements Context {
    private static final Logger logger = LoggerFactory.getLogger(ConversationContextImpl.class);
    private final ThreadLocal<Map<Contextual<?>, Object>> instances;
    private final ReadWriteLock lock;
    private final Conversation conversation;

    public ConversationContextImpl(ReadWriteLock lock,
                                   Conversation conversation) {
        this.instances = new InheritableThreadLocal<>() {
            @Override
            protected Map<Contextual<?>, Object> initialValue() {
                return new ConcurrentHashMap<>();
            }
        };
        this.instances.get();
        this.lock = lock;
        this.conversation = conversation;
    }

    @Override
    public Class<? extends Annotation> scope() {
        return ConversationScoped.class;
    }

    @Override
    public <T> T get(Contextual<T> contextual) {
        this.lock.readLock().lock();
        try {
            return this.resolve(contextual);
        } finally {
            this.lock.readLock().unlock();
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T resolve(Contextual<T> contextual) {
        if (this.conversation.isTransient()) {
            throw new IllegalStateException();
        }
        return (T) this.instances.get().computeIfAbsent(
            contextual, x -> contextual.create(null));
    }
}
