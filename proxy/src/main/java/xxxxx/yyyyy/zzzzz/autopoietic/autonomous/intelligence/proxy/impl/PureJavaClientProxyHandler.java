package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy.Contextual;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy.ProxyContainer;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PureJavaClientProxyHandler implements InvocationHandler {
    private static final Logger logger = LoggerFactory.getLogger(PureJavaClientProxyHandler.class);
    private final Contextual<?> contextual;
    private final ProxyContainer proxyContainer;

    public PureJavaClientProxyHandler(Contextual<?> contextual, ProxyContainer proxyContainer) {
        this.contextual = contextual;
        this.proxyContainer = proxyContainer;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getDeclaringClass() == Object.class) {
            return method.invoke(this, args);
        }
        return this.invoke(proxy, method, args, x -> {
            try {
                return method.invoke(x, args);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private Object invoke(Object proxy, Method method, Object[] args, Function<Object, Object> f) {
        PureJavaContextual<?> pureJavaContextual = (PureJavaContextual<?>) this.contextual;
        Object instance = this.proxyContainer.context(pureJavaContextual.scope()).get(pureJavaContextual);
        logger.trace(String.format("[%-17s] [Proxy:%08x] -> [Instance:%08x] [%-8s] %s#%s",
                pureJavaContextual.scope().getSimpleName(),
                System.identityHashCode(proxy),
                System.identityHashCode(instance),
                pureJavaContextual.qualifiers().stream()
                        .map(x -> "@" + x.annotationType().getSimpleName())
                        .collect(Collectors.joining(" ")),
                pureJavaContextual.type().getTypeName(),
                method.getName()));
        return f.apply(instance);
    }
}
