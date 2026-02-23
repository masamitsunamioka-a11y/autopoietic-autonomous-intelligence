package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy.impl;

import jakarta.enterprise.context.Dependent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy.Context;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy.Contextual;

import java.lang.annotation.Annotation;

public class DependentContextImpl implements Context {
    private static final Logger logger = LoggerFactory.getLogger(DependentContextImpl.class);

    public DependentContextImpl() {
    }

    @Override
    public Class<? extends Annotation> scope() {
        return Dependent.class;
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

    private <T> T resolve(Contextual<T> contextual) {
        return (T) contextual.create(null);
    }
}
