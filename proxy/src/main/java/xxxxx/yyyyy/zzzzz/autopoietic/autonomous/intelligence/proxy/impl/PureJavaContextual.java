package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Qualifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy.AnnotatedType;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy.Contextual;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy.CreationalContext;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy.ProxyContainer;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class PureJavaContextual<T> implements Contextual<T> {
    private static final Logger logger = LoggerFactory.getLogger(PureJavaContextual.class);
    private final AnnotatedType<T> annotatedType;
    private final ProxyContainer proxyContainer;
    private final Type type;
    private final Class<? extends Annotation> scope;
    private final Set<? extends Annotation> qualifiers;

    public PureJavaContextual(AnnotatedType<T> annotatedType, ProxyContainer proxyContainer) {
        this.annotatedType = annotatedType;
        this.proxyContainer = proxyContainer;
        this.type = this.annotatedType.typeClosure().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "CRITICAL: No interface found for " + annotatedType.javaClass().getName()));
        /// Get scope from ClassWrapper.
        this.scope = this.annotatedType.isAnnotationPresent(ApplicationScoped.class)
                ? ApplicationScoped.class
                : ApplicationScoped.class; /// ULCDI Default
        Set<Annotation> x = this.annotatedType.annotations().stream()
                .filter(a -> a.annotationType().isAnnotationPresent(Qualifier.class))
                .collect(Collectors.toSet());
        if (x.isEmpty()) {
            x.add(jakarta.enterprise.inject.Default.Literal.INSTANCE);
        }
        this.qualifiers = x;
    }

    @Override
    public T create(CreationalContext<T> creationalContext) {
        try {
            ClassWrapper<T> wrapper = (ClassWrapper<T>) this.annotatedType;
            Object[] args = wrapper.constructorParameters().stream()
                    .map(x -> this.proxyContainer.get(
                            x.getParameterizedType(),
                            wrapper.parameterQualifiers(x))
                    )
                    .toArray();
            return (T) wrapper.annotatedConstructor().newInstance(args);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("CRITICAL: Instantiation failed for " +
                    this.annotatedType.javaClass().getName(), e);
        }
    }

    public Type type() {
        return this.type;
    }

    public Class<? extends Annotation> scope() {
        return this.scope;
    }

    public Set<? extends Annotation> qualifiers() {
        return this.qualifiers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PureJavaContextual<?> that)) return false;
        return Objects.equals(this.type(), that.type()) &&
                Objects.equals(this.qualifiers, that.qualifiers());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.type(), this.qualifiers);
    }
}
