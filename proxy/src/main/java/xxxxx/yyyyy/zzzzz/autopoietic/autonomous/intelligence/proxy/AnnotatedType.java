package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Set;

/// https://jakarta.ee/specifications/cdi/4.1/apidocs/jakarta/enterprise/inject/spi/annotated
/// https://github.com/jakartaee/cdi/blob/4.1.0/api/src/main/java/jakarta/enterprise/inject/spi/Annotated.java
/// https://jakarta.ee/specifications/cdi/4.1/apidocs/jakarta/enterprise/inject/spi/annotatedtype
/// https://github.com/jakartaee/cdi/blob/4.1.0/api/src/main/java/jakarta/enterprise/inject/spi/AnnotatedType.java
public interface AnnotatedType<T> {
    /// --- Derived from Annotated ---
    Set<Type> typeClosure();

    Set<Annotation> annotations();

    boolean isAnnotationPresent(Class<? extends Annotation> annotationType);

    /// --- Derived from AnnotatedType ---
    Class<T> javaClass();
}
