package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy.impl;

import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy.Context;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy.Contextual;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ApplicationContextImpl implements Context {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationContextImpl.class);
    private final Map<Contextual<?>, Object> instances;

    public ApplicationContextImpl() {
        this.instances = new ConcurrentHashMap<>();
    }

    @Override
    public Class<? extends Annotation> scope() {
        return ApplicationScoped.class;
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
        return (T) this.instances.computeIfAbsent(
            contextual, x -> contextual.create(null));
    }
}
