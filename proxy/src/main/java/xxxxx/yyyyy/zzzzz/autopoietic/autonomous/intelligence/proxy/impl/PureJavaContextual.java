package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Default;
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

    public PureJavaContextual(AnnotatedType<T> annotatedType,
                              ProxyContainer proxyContainer) {
        this.annotatedType = annotatedType;
        this.proxyContainer = proxyContainer;
    }

    @Override
    public T create(CreationalContext<T> creationalContext) {
        /// FIXME
        try {
            ClassWrapper<T> wrapper = (ClassWrapper<T>) this.annotatedType;
            Object[] args = wrapper.constructorParameters().stream()
                .map(x -> this.proxyContainer.get(
                    x.getParameterizedType(),
                    wrapper.parameterQualifiers(x)))
                .toArray();
            return (T) wrapper.annotatedConstructor().newInstance(args);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Instantiation failed for " +
                this.annotatedType.javaClass().getName(), e);
        }
    }

    public Type type() {
        return this.annotatedType.typeClosure().stream()
            .findFirst()
            .orElseThrow();
    }

    public Class<? extends Annotation> scope() {
        /// Get scope from ClassWrapper.
        return ApplicationScoped.class;
    }

    public Set<? extends Annotation> qualifiers() {
        var qualifiers = this.annotatedType.annotations().stream()
            .filter(x -> x.annotationType().isAnnotationPresent(Qualifier.class))
            .collect(Collectors.toSet());
        return (!qualifiers.isEmpty())
            ? qualifiers
            : Set.of(Default.Literal.INSTANCE);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PureJavaContextual<?> that)) return false;
        return Objects.equals(this.type(), that.type())
            && Objects.equals(this.qualifiers(), that.qualifiers());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.type(), this.qualifiers());
    }
}
