package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy.impl;

import jakarta.enterprise.context.NormalScope;
import jakarta.enterprise.inject.Default;
import jakarta.inject.Inject;
import jakarta.inject.Qualifier;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy.AnnotatedType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/// FIXME
public class ClassWrapper<T> implements AnnotatedType<T> {
    private final Class<T> javaClass;

    public ClassWrapper(Class<T> javaClass) {
        this.javaClass = javaClass;
    }

    @Override
    public Set<Type> typeClosure() {
        Set<Type> types = new LinkedHashSet<>();
        Class<?> current = this.javaClass;
        while (current != null && current != Object.class) {
            Collections.addAll(types, current.getGenericInterfaces());
            current = current.getSuperclass();
        }
        return types;
    }

    @Override
    public Set<Annotation> annotations() {
        return Arrays.stream(this.javaClass.getAnnotations()).collect(Collectors.toSet());
    }

    @Override
    public boolean isAnnotationPresent(Class<? extends Annotation> annotationType) {
        return this.javaClass.isAnnotationPresent(annotationType);
    }

    @Override
    public Class<T> javaClass() {
        return this.javaClass;
    }

    /// -----
    public boolean isInjectable() {
        if (this.typeClosure().isEmpty()) {
            return false;
        }
        long scopeCount = this.filterMetaAnnotationType(
                this.javaClass.getAnnotations(), NormalScope.class)
            .stream()
            .count();
        if (scopeCount != 1) {
            return false;
        }
        if (this.javaClass.getInterfaces().length == 0) {
            return false;
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    public Constructor<T> annotatedConstructor() {
        var constructors = this.javaClass.getConstructors();
        return (Constructor<T>) Arrays.stream(constructors)
            .filter(x -> x.isAnnotationPresent(Inject.class))
            .findFirst()
            .orElse(constructors[0]);
    }

    public List<Parameter> constructorParameters() {
        return List.of(this.annotatedConstructor().getParameters());
    }

    public Annotation parameterQualifiers(Parameter parameter) {
        return Arrays.stream(parameter.getAnnotations())
            .filter(x -> x.annotationType().isAnnotationPresent(Qualifier.class))
            .findFirst()
            .orElse(Default.Literal.INSTANCE);
    }

    private Set<Class<? extends Annotation>> filterMetaAnnotationType(
        Annotation[] annotations, Class<? extends Annotation> metaAnnotationType) {
        return Arrays.stream(annotations)
            .map(Annotation::annotationType)
            .filter(xyz -> xyz.isAnnotationPresent(metaAnnotationType))
            .collect(Collectors.toSet());
    }
}
