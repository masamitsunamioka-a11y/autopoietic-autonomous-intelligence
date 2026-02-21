package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy.ClientProxyProvider;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy.Contextual;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy.ProxyContainer;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;

public class PureJavaClientProxyProvider implements ClientProxyProvider {
    private static final Logger logger = LoggerFactory.getLogger(PureJavaClientProxyProvider.class);
    private final ProxyContainer proxyContainer;

    public PureJavaClientProxyProvider(ProxyContainer proxyContainer) {
        this.proxyContainer = proxyContainer;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T provide(Contextual<T> contextual) {
        return (T) Proxy.newProxyInstance(
            Thread.currentThread().getContextClassLoader(),
            this.rawTypes(contextual),
            new PureJavaClientProxyHandler(contextual, this.proxyContainer));
    }

    private <T> Class<?>[] rawTypes(Contextual<T> contextual) {
        return ((PureJavaContextual<T>) contextual).types().stream()
            .map(this::rawType)
            .filter(Class::isInterface)
            .distinct()
            .toArray(Class<?>[]::new);
    }

    private Class<?> rawType(Type type) {
        return (type instanceof ParameterizedType parameterizedType)
            ? (Class<?>) parameterizedType.getRawType()
            : (Class<?>) type;
    }
}
