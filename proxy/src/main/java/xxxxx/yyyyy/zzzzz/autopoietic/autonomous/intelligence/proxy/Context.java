package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy;

import java.lang.annotation.Annotation;

/// https://jakarta.ee/specifications/cdi/4.1/apidocs/jakarta/enterprise/context/spi/context
/// https://github.com/jakartaee/cdi/blob/4.1.0/api/src/main/java/jakarta/enterprise/context/spi/Context.java
public interface Context {
    <T> T get(Contextual<T> contextual);

    Class<? extends Annotation> scope();
}
