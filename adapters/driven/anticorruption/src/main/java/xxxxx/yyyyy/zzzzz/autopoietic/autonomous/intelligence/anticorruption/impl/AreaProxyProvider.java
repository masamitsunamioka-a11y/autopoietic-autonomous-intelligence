package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.ProxyProvider;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Serializer;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Area;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.Utility.actualTypeArguments;

@ApplicationScoped
public class AreaProxyProvider implements ProxyProvider<Area> {
    private static final Logger logger = LoggerFactory.getLogger(AreaProxyProvider.class);
    private final Serializer serializer;

    @Inject
    public AreaProxyProvider(Serializer serializer) {
        this.serializer = serializer;
    }

    private static record InternalArea(
        String name,
        String tuning,
        List<String> neurons,
        List<String> effectors) {
    }

    @Override
    public Area provide(String json) {
        var reference = new AtomicReference<InternalArea>(
            this.serializer.deserialize(json, InternalArea.class));
        return (Area) Proxy.newProxyInstance(
            Thread.currentThread().getContextClassLoader(),
            new Class<?>[]{actualTypeArguments(this.getClass())},
            (proxy, method, args) -> {
                var area = reference.get();
                return switch (method.getName()) {
                    case "toString" -> this.serializer.serialize(area);
                    case "hashCode" -> System.identityHashCode(proxy);
                    case "equals" -> this.equals(proxy, args);
                    default -> {
                        yield InternalArea.class
                            .getMethod(method.getName())
                            .invoke(area);
                    }
                };
            }
        );
    }

    private boolean equals(Object proxy, Object[] args) {
        return args != null && args.length == 1 && proxy == args[0];
    }
}
