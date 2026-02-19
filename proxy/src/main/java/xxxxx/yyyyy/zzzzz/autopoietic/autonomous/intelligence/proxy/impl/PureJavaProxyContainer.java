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
import java.util.stream.Collectors;

public class PureJavaProxyContainer implements ProxyContainer {
    private static final Logger logger = LoggerFactory.getLogger(PureJavaProxyContainer.class);
    private final ClassScanner classScanner;
    private final ClientProxyProvider clientProxyProvider;
    private final Map<Class<? extends Annotation>, Context> contexts;
    private final Map<Contextual<?>, Object> proxies;
    private final ReadWriteLock lock = new ReentrantReadWriteLock(true);

    public PureJavaProxyContainer(ClassScanner classScanner) {
        this.classScanner = classScanner;
        this.clientProxyProvider = new PureJavaClientProxyProvider(this);
        this.contexts = new ConcurrentHashMap<>();
        this.proxies = new ConcurrentHashMap<>();
        this.reload();
    }

    private void reconcile() {
        /// this.contexts.clear();
        /// this.proxies.clear();
        /// this.reload();
    }

    private void reload() {
        this.lock.writeLock().lock();
        try {
            this.register();
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    private void register() {
        this.contexts.put(ApplicationScoped.class, new PureJavaContextApplicationScoped());
        /// @formatter:off
        var contextuals = this.classScanner.scan().stream()
            .map(ClassWrapper::new)
            .filter(ClassWrapper::isInjectable)
            .map(x -> new PureJavaContextual<>(x, this))
            .collect(Collectors.toSet());
        contextuals.stream()
            .filter(x -> x.scope().equals(ApplicationScoped.class))
            .forEach(x -> {
                this.proxies.put(x, this.clientProxyProvider.provide(x));
            });
        /// @formatter:on
    }

    @Override
    public <T> T get(Type type, Annotation... qualifiers) {
        this.lock.readLock().lock();
        try {
            return this.internalGet(type, qualifiers);
        } finally {
            this.lock.readLock().unlock();
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T internalGet(Type type, Annotation[] qualifiers) {
        /// @formatter:off
        var orDefault = (qualifiers.length > 0)
            ? Set.of(qualifiers)
            : Set.of(Default.Literal.INSTANCE);
        return (T) this.proxies.keySet().stream()
            .map(x -> (PureJavaContextual<?>) x)
            .filter(x -> x.type().equals(type))
            .filter(x -> x.qualifiers().equals(orDefault))
            .findFirst()
            .map(this.proxies::get)
            .orElseThrow(IllegalArgumentException::new);
        /// @formatter:on
    }

    @Override
    public Context context(Class<? extends Annotation> scope) {
        return this.contexts.get(scope);
    }
}
