package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy.impl;

import jakarta.enterprise.inject.Default;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PureJavaProxyContainer implements ProxyContainer {
    private static final Logger logger = LoggerFactory.getLogger(PureJavaProxyContainer.class);
    private final ClassScanner classScanner;
    private final ClientProxyProvider clientProxyProvider;
    private final Map<Class<? extends Annotation>, Context> contextCache;
    private final Map<Type, List<Contextual<?>>> contextualCache;
    private final Map<Contextual<?>, Object> proxyCache;

    public PureJavaProxyContainer(ClassScanner classScanner) {
        this.classScanner = classScanner;
        this.clientProxyProvider = new PureJavaClientProxyProvider(this);
        this.contextCache = new ConcurrentHashMap<>();
        this.contextualCache = new ConcurrentHashMap<>();
        this.proxyCache = new ConcurrentHashMap<>();
        ///
        this.classScanner.scan().stream()
                .map(ClassWrapper::new)
                .filter(ClassWrapper::isInjectable)
                .map(x -> new PureJavaContextual<>(x, this))
                .flatMap(x -> {
                    return Stream.of(x.scope())
                            .map(scope -> Map.entry(scope, x));
                })
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toList())))
                ///
                .forEach((scopeType, contextuals) -> {
                    switch (scopeType.getSimpleName()) {
                        case "ApplicationScoped" -> {
                            this.contextCache.put(
                                    scopeType,
                                    new PureJavaContextApplicationScoped()
                            );
                            contextuals.forEach(this::register);
                        }
                        default -> throw new IllegalStateException(
                                "CRITICAL: Unsupported scope: " + scopeType.getName());
                    }
                });
    }

    private <T> void register(Contextual<T> contextual) {
        this.contextualCache.computeIfAbsent(
                ((PureJavaContextual<T>) contextual).type(),
                x -> new ArrayList<>()).add(contextual);
        this.proxyCache.put(
                contextual,
                this.clientProxyProvider.provide(contextual)
        );
    }

    /// @SuppressWarnings("unchecked")
    @Override
    public <T> T get(Type type, Annotation... qualifiers) {
        List<Contextual<?>> candidates = this.contextualCache.get(type);
        if (candidates == null) {
            throw new IllegalStateException("""
                    CRITICAL: Bean not found for type.
                    Type: %s
                    """.formatted(type));
        }
        Set<Annotation> requiredQualifiers = (qualifiers.length == 0)
                ? Set.of(Default.Literal.INSTANCE)
                : Arrays.stream(qualifiers).collect(Collectors.toSet());
        return (T) Objects.requireNonNull(this.proxyCache.get(
                /// @formatter:off
                candidates.stream()
                    .map(x -> (PureJavaContextual<?>) x)
                    .filter(x -> x.qualifiers().equals(requiredQualifiers))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("""
                        CRITICAL: No match for type with qualifiers.
                        Type: %s
                        Qualifiers: %s
                        """.formatted(type, requiredQualifiers)))
                /// @formatter:on
        ));
    }

    @Override
    public Context context(Class<? extends Annotation> scope) {
        return this.contextCache.get(scope);
    }
}
