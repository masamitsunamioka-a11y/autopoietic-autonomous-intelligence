package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.ProxyProvider;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Resource;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Serializer;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Entity;

import java.lang.reflect.Proxy;
import java.util.Map;

public class JsonObjectProxyProvider<I extends Entity> implements ProxyProvider<I> {
    private static final Logger logger = LoggerFactory.getLogger(JsonObjectProxyProvider.class);
    private final Serializer serializer;
    private final Class<I> type;

    public JsonObjectProxyProvider(Serializer serializer, Class<I> type) {
        this.serializer = serializer;
        this.type = type;
    }

    @Override
    @SuppressWarnings("unchecked")
    public I provide(Resource resource) {
        Map<String, Object> map = this.serializer.deserialize(resource.content(), Map.class);
        var string = this.serializer.serialize(map);
        return (I) Proxy.newProxyInstance(
            Thread.currentThread().getContextClassLoader(),
            new Class<?>[]{this.type},
            (proxy, method, args) -> {
                return switch (method.getName()) {
                    case "toString" -> string;
                    case "hashCode" -> System.identityHashCode(proxy);
                    case "equals" -> {
                        yield args != null && args.length == 1 && proxy == args[0];
                    }
                    default -> map.get(method.getName());
                };
            }
        );
    }
}
