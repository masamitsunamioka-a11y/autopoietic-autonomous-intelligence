package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy.impl;

import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy.ClientProxyProvider;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy.Contextual;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.proxy.ProxyContainer;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;

public class PureJavaClientProxyProvider implements ClientProxyProvider {
    private final ProxyContainer proxyContainer;

    public PureJavaClientProxyProvider(ProxyContainer proxyContainer) {
        this.proxyContainer = proxyContainer;
    }

    /// @SuppressWarnings("unchecked")
    @Override
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
