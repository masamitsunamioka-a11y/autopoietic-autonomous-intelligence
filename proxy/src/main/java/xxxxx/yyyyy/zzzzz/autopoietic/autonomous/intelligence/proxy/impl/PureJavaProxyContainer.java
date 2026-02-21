package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Default;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class PureJavaProxyContainer implements ProxyContainer {
    private static final Logger logger = LoggerFactory.getLogger(PureJavaProxyContainer.class);
    private final ClassScanner classScanner;
    private final ClientProxyProvider clientProxyProvider;
    private final Map<Class<? extends Annotation>, Context> contexts;
    private final Map<Contextual<?>, Object> proxies;
    private final ReadWriteLock lock;

    public PureJavaProxyContainer(ClassScanner classScanner) {
        this.classScanner = classScanner;
        this.clientProxyProvider = new PureJavaClientProxyProvider(this);
        this.contexts = new ConcurrentHashMap<>();
        this.proxies = new ConcurrentHashMap<>();
        this.lock = new ReentrantReadWriteLock(true);
        this.provision();
    }

    private void reconcile() {
    }

    private void provision() {
        this.lock.writeLock().lock();
        try {
            this.allocate();
            this.wire();
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    private void allocate() {
        this.contexts.put(ApplicationScoped.class, new PureJavaContext(this.lock));
    }

    private void wire() {
        this.classScanner.scan().stream()
            .map(ClassWrapper::new)
            .filter(ClassWrapper::isInjectable)
            .map(x -> new PureJavaContextual<>(x, this))
            .forEach(x -> {
                this.proxies.put(x, this.clientProxyProvider.provide(x));
            });
    }

    @Override
    public <T> T get(Type type, Annotation... qualifiers) {
        this.lock.readLock().lock();
        try {
            return this.resolve(type, qualifiers);
        } finally {
            this.lock.readLock().unlock();
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T resolve(Type type, Annotation[] qualifiers) {
        var orDefault = (qualifiers.length > 0)
            ? Set.of(qualifiers)
            : Set.of(Default.Literal.INSTANCE);
        return (T) this.proxies.keySet().stream()
            .map(x -> (PureJavaContextual<?>) x)
            .filter(x -> x.types().contains(type))
            .filter(x -> x.qualifiers().equals(orDefault))
            .findFirst()
            .map(this.proxies::get)
            .orElseThrow();
    }

    @Override
    public Context context(Class<? extends Annotation> scope) {
        return this.contexts.get(scope);
    }
}
