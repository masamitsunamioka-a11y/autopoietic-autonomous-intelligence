package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/// https://jakarta.ee/specifications/cdi/4.1/apidocs/jakarta/enterprise/inject/spi/beancontainer
/// https://github.com/jakartaee/cdi/blob/4.1.0/api/src/main/java/jakarta/enterprise/inject/spi/BeanContainer.java
public interface ProxyContainer {
    /// This returns a bean directly instead of a Set of Bean.
    /// To be aligned with CDI 4.1 getBeans in the future.
    <T> T get(Type type, Annotation... qualifiers);

    Context context(Class<? extends Annotation> scope);
}
