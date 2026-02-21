package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy.impl;

import jakarta.enterprise.context.NormalScope;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy.AnnotatedType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.Collectors.toUnmodifiableSet;

public class ClassWrapper<T> implements AnnotatedType<T> {
    private static final Logger logger = LoggerFactory.getLogger(ClassWrapper.class);
    private final Class<T> javaClass;

    public ClassWrapper(Class<T> javaClass) {
        this.javaClass = javaClass;
    }

    @Override
    public Set<Type> typeClosure() {
        return Stream.of(this.javaClass)
            .<Type>mapMulti(this::typeClosure)
            .collect(toUnmodifiableSet());
    }

    /// org.jboss.weld.util.reflection.HierarchyDiscovery
    private void typeClosure(Type v, Consumer<Type> c) {
        if (v == null) {
            return;
        }
        c.accept(v);
        switch (v) {
            case ParameterizedType x -> {
                this.typeClosure(x.getRawType(), c);
            }
            case Class<?> x -> {
                this.typeClosure(x.getGenericSuperclass(), c);
                stream(x.getGenericInterfaces())
                    .forEach(y -> this.typeClosure(y, c));
            }
            default -> {
            }
        }
    }

    @Override
    public Set<Annotation> annotations() {
        return stream(this.javaClass.getAnnotations())
            .collect(toSet());
    }

    public boolean isInjectable() {
        return stream(this.javaClass.getAnnotations())
            .map(Annotation::annotationType)
            .filter(x -> x.isAnnotationPresent(NormalScope.class))
            .count() == 1;
    }

    public T instantiate(Function<Parameter, Object> injector) {
        try {
            var constructor = this.constructor();
            return constructor.newInstance(
                stream(constructor.getParameters())
                    .map(injector)
                    .toArray());
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private Constructor<T> constructor() {
        var constructors = this.javaClass.getConstructors();
        return (Constructor<T>) stream(constructors)
            .filter(x -> x.isAnnotationPresent(Inject.class))
            .findFirst()
            .orElse(constructors[0]);
    }
}
