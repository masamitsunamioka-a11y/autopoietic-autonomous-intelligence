package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.ConversationScoped;
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

public class ProxyContainerImpl implements ProxyContainer {
    private static final Logger logger = LoggerFactory.getLogger(ProxyContainerImpl.class);
    private final ClassScanner classScanner;
    private final ClientProxyProvider clientProxyProvider;
    private final Map<Class<? extends Annotation>, Context> contexts;
    private final Map<Contextual<?>, Object> proxies;
    private final ReadWriteLock lock;

    public ProxyContainerImpl(ClassScanner classScanner) {
        this.classScanner = classScanner;
        this.clientProxyProvider = new ClientProxyProviderImpl(this);
        this.contexts = new ConcurrentHashMap<>();
        this.proxies = new ConcurrentHashMap<>();
        this.lock = new ReentrantReadWriteLock(true);
        this.reconcile();
    }

    /// 123456789_123456789_123456789_123456789_123456789_123456789_
    private void reconcile() {
        this.lock.writeLock().lock();
        try {
            this.proxies.clear();
            this.contexts.clear();
            this.discover();
            this.activate();
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    /// 123456789_123456789_123456789_123456789_123456789_123456789_
    private void discover() {
        var contextuals = this.classScanner.scan().stream()
            .map(AnnotatedTypeImpl::new)
            .filter(AnnotatedTypeImpl::isInjectable)
            .map(x -> new ContextualImpl<>(x, this))
            .collect(Collectors.toSet());
        contextuals.forEach(x -> {
            this.proxies.put(x, this.clientProxyProvider.provide(x));
        });
    }

    /// 123456789_123456789_123456789_123456789_123456789_123456789_
    private void activate() {
        this.contexts.put(ApplicationScoped.class,
            new ApplicationContextImpl());
        this.contexts.put(ConversationScoped.class,
            this.get(ConversationContextImpl.class));
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
        ///
        return (T) this.proxies.keySet().stream()
            .map(x -> (ContextualImpl<?>) x)
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
