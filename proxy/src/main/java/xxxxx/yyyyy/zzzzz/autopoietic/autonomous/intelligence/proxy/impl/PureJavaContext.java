package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy.impl;

import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy.Context;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy.Contextual;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;

public class PureJavaContext implements Context {
    private static final Logger logger = LoggerFactory.getLogger(PureJavaContext.class);
    private final Map<Contextual<?>, Object> instances;
    private final ReadWriteLock lock;

    public PureJavaContext(ReadWriteLock lock) {
        this.instances = new ConcurrentHashMap<>();
        this.lock = lock;
    }

    @Override
    public Class<? extends Annotation> scope() {
        return ApplicationScoped.class;
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
        return (T) this.instances.computeIfAbsent(
            contextual, x -> contextual.create(null));
    }
}
