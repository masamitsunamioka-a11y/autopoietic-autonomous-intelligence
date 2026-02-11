package xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.proxy;

/// https://jakarta.ee/specifications/cdi/4.1/apidocs/jakarta/enterprise/context/spi/contextual
/// https://github.com/jakartaee/cdi/blob/4.1.0/api/src/main/java/jakarta/enterprise/context/spi/Contextual.java
public interface Contextual<T> {
    T create(CreationalContext<T> creationalContext);
}
