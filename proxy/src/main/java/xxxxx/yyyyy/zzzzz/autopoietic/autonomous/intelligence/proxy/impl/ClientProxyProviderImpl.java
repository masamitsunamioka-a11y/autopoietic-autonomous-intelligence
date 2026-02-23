package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy.ClientProxyProvider;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy.Contextual;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy.ProxyContainer;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;

public class ClientProxyProviderImpl implements ClientProxyProvider {
    private static final Logger logger = LoggerFactory.getLogger(ClientProxyProviderImpl.class);
    private final ProxyContainer proxyContainer;

    public ClientProxyProviderImpl(ProxyContainer proxyContainer) {
        this.proxyContainer = proxyContainer;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T provide(Contextual<T> contextual) {
        return (T) Proxy.newProxyInstance(
            Thread.currentThread().getContextClassLoader(),
            this.rawTypes(contextual),
            new ClientProxyHandlerImpl(contextual, this.proxyContainer));
    }

    private <T> Class<?>[] rawTypes(Contextual<T> contextual) {
        return ((ContextualImpl<T>) contextual).types().stream()
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
