package xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.proxy.impl;

import xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.proxy.ClientProxyProvider;
import xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.proxy.Contextual;
import xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.proxy.ProxyContainer;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;

public class PureJavaClientProxyProvider implements ClientProxyProvider {
    private final ProxyContainer proxyContainer;

    public PureJavaClientProxyProvider(ProxyContainer proxyContainer) {
        this.proxyContainer = proxyContainer;
    }

    @Override
    /// @SuppressWarnings("unchecked")
    public <T> T provide(Contextual<T> contextual) {
        Type type = ((PureJavaContextual<T>) contextual).type();
        Class<?> rawType = (type instanceof ParameterizedType parameterizedType)
                ? (Class<?>) parameterizedType.getRawType()
                : (Class<?>) type;
        return (T) Proxy.newProxyInstance(
                Thread.currentThread().getContextClassLoader(),
                new Class<?>[]{rawType},
                new PureJavaClientProxyHandler(contextual, this.proxyContainer)
        );
    }
}
