package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy.Contextual;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy.ProxyContainer;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.lang.System.identityHashCode;

public class PureJavaClientProxyHandler implements InvocationHandler {
    private static final Logger logger = LoggerFactory.getLogger(PureJavaClientProxyHandler.class);
    private final Contextual<?> contextual;
    private final ProxyContainer proxyContainer;

    public PureJavaClientProxyHandler(Contextual<?> contextual, ProxyContainer proxyContainer) {
        this.contextual = contextual;
        this.proxyContainer = proxyContainer;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] arguments) throws Throwable {
        if (method.getDeclaringClass() == Object.class) {
            return method.invoke(this, arguments);
        }
        Object instance = this.proxyContainer
            .context(this.reveal(this.contextual).scope())
            .get(this.contextual);
        InvocationContext invocationContext =
            new InvocationContext(proxy, method, arguments, instance);
        return this.invoke(invocationContext, x -> {
            try {
                return method.invoke(x, arguments);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private <T> PureJavaContextual<T> reveal(Contextual<T> contextual) {
        return (PureJavaContextual<T>) contextual;
    }

    private static record InvocationContext(
        Object proxy,
        Method method,
        Object[] arguments,
        Object instance) {
    }

    private Object invoke(InvocationContext ic, Function<Object, Object> f) {
        String qualifiers = this.reveal(contextual).qualifiers().stream()
            .map(Annotation::annotationType)
            .map(this::name)
            .collect(Collectors.joining(" "));
        logger.trace(String.format(
            "[%-17s] [Proxy:%08x] -> [Instance:%08x] [%-7s] %s#%s",
            this.name(this.reveal(contextual).scope()),
            identityHashCode(ic.proxy()),
            identityHashCode(ic.instance()),
            qualifiers,
            this.name(this.reveal(contextual).type()),
            ic.method().getName()));
        return f.apply(ic.instance());
    }

    private String name(Object object) {
        String fullyQualifiedName = (switch (object) {
            case null -> "null";
            case Class<?> x -> x.getName();
            case Type x -> x.getTypeName();
            default -> object.toString();
        });
        return fullyQualifiedName.replaceAll("(\\w+\\.)+", "");
    }
}
