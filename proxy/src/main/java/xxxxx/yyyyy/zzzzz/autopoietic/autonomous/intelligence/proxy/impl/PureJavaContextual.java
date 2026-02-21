package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy.impl;

import jakarta.enterprise.context.NormalScope;
import jakarta.enterprise.inject.Default;
import jakarta.inject.Qualifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy.AnnotatedType;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy.Contextual;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy.CreationalContext;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy.ProxyContainer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.Set;

import static java.util.Arrays.stream;

/// https://jakarta.ee/specifications/cdi/4.1/apidocs/jakarta/enterprise/inject/spi/beanattributes
/// https://github.com/jakartaee/cdi/blob/4.1.0/api/src/main/java/jakarta/enterprise/inject/spi/BeanAttributes.java
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
        var unwrapped = (ClassWrapper<T>) this.annotatedType;
        return unwrapped.instantiate(x -> {
            return this.inject(x.getParameterizedType(), x.getAnnotations());
        });
    }

    private T inject(Type type, Annotation[] annotations) {
        var qualifiers = this.orDefault(annotations);
        return this.proxyContainer.get(type, qualifiers);
    }

    private Annotation[] orDefault(Annotation[] annotations) {
        var qualifiers = stream(annotations)
            .filter(x -> x.annotationType()
                .isAnnotationPresent(Qualifier.class))
            .toArray(Annotation[]::new);
        return (qualifiers.length > 0)
            ? qualifiers
            : new Annotation[]{Default.Literal.INSTANCE};
    }

    /// @Override
    public Set<Type> types() {
        return this.annotatedType.typeClosure();
    }

    /// @Override
    public Set<Annotation> qualifiers() {
        return Set.of(this.orDefault(
            this.annotatedType.annotations().toArray(new Annotation[0])));
    }

    /// @Override
    public Class<? extends Annotation> scope() {
        return this.annotatedType.annotations().stream()
            .map(Annotation::annotationType)
            .filter(x -> x.isAnnotationPresent(NormalScope.class))
            .findFirst()
            .orElseThrow();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PureJavaContextual<?> that)) {
            return false;
        }
        return this.types().equals(that.types())
            && this.qualifiers().equals(that.qualifiers());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.types(), this.qualifiers());
    }
}
