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

import static java.util.stream.Collectors.toSet;

public class ProxyContainerImpl implements ProxyContainer {
    private static final Logger logger = LoggerFactory.getLogger(ProxyContainerImpl.class);
    private final Map<Class<? extends Annotation>, Context> contexts;
    private final Map<Contextual<?>, Object> proxies;
    private final ReadWriteLock lock;
    private final ClassScanner classScanner;
    private final ClientProxyProvider clientProxyProvider;

    public ProxyContainerImpl(ClassScanner classScanner) {
        this.contexts = new ConcurrentHashMap<>();
        this.proxies = new ConcurrentHashMap<>();
        this.lock = new ReentrantReadWriteLock(true);
        this.classScanner = classScanner;
        this.clientProxyProvider = new ClientProxyProviderImpl(this);
        this.reconcile();
    }

    /// In the future, this container will implement a reconciliation loop.
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

    private void discover() {
        var contextuals = this.classScanner.scan().stream()
            .map(AnnotatedTypeImpl::new)
            .filter(AnnotatedTypeImpl::isInjectable)
            .map(x -> new ContextualImpl<>(x, this))
            .collect(toSet());
        contextuals
            .forEach(x -> {
                this.proxies.put(x,
                    this.clientProxyProvider.provide(x));
            });
    }

    private void activate() {
        this.contexts.put(ApplicationScoped.class,
            new ApplicationContextImpl(this.lock));
        this.contexts.put(ConversationScoped.class,
            new ConversationContextImpl(this.lock, this.get(Conversation.class)));
        /// this.contexts.put(SessionScoped.class,
        ///     new SessionContextImpl());
        /// this.contexts.put(RequestScoped.class,
        ///     new RequestContextImpl());
        /// this.contexts.put(Dependent.class,
        ///     new DependentContextImpl());
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
        var qualified = this.proxies.keySet().stream()
            .map(x -> (ContextualImpl<?>) x)
            .filter(x -> {
                return x.types().equals(type);
            })
            .filter(x -> {
                return x.qualifiers().equals(orDefault);
            })
            .map(this.proxies::get)
            .toList();
        return (T) qualified.getFirst();
    }

    @Override
    public Context context(Class<? extends Annotation> scope) {
        return this.contexts.get(scope);
    }
}
