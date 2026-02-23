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

import static java.lang.System.identityHashCode;
import static java.util.stream.Collectors.joining;

public class ClientProxyHandlerImpl implements InvocationHandler {
    private static final Logger logger = LoggerFactory.getLogger(ClientProxyHandlerImpl.class);
    private final Contextual<?> contextual;
    private final ProxyContainer proxyContainer;

    public ClientProxyHandlerImpl(Contextual<?> contextual,
                                  ProxyContainer proxyContainer) {
        this.contextual = contextual;
        this.proxyContainer = proxyContainer;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] arguments) throws Throwable {
        if (method.getDeclaringClass() == Object.class) {
            return method.invoke(this, arguments);
        }
        var unwrapped = (ContextualImpl<?>) this.contextual;
        var instance = this.proxyContainer
            .context(unwrapped.scope())
            .get(this.contextual);
        return this.invoke(
            new InvocationContext(proxy, method, arguments, instance), x -> {
                try {
                    return method.invoke(x, arguments);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    private static record InvocationContext(
        Object proxy,
        Method method,
        Object[] arguments,
        Object instance) {
    }

    private Object invoke(InvocationContext ic, Function<Object, Object> f) {
        var unwrapped = (ContextualImpl<?>) this.contextual;
        logger.trace(String.format(
            "[%-17s] [Proxy:%08x] -> [Instance:%08x] [%-7s] %s#%s",
            this.name(unwrapped.scope()),
            identityHashCode(ic.proxy()),
            identityHashCode(ic.instance()),
            unwrapped.qualifiers().stream()
                .map(Annotation::annotationType)
                .map(this::name)
                .collect(joining(" ")),
            this.name(unwrapped.types()),
            ic.method().getName()));
        return f.apply(ic.instance());
    }

    private String name(Object object) {
        var fullyQualifiedName = (switch (object) {
            case null -> "null";
            case Class<?> x -> x.getName();
            case Type x -> x.getTypeName();
            default -> object.toString();
        });
        return fullyQualifiedName.replaceAll("(\\w+\\.)+", "");
    }
}
