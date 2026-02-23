package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.JsonCodec;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.ProxyProvider;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Effector;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Schema;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.Util.actualTypeArguments;

@ApplicationScoped
public class SchemaProxyProvider implements ProxyProvider<Schema> {
    private static final Logger logger = LoggerFactory.getLogger(SchemaProxyProvider.class);
    private final Repository<Effector> effectorRepository;
    private final JsonCodec jsonCodec;

    @Inject
    public SchemaProxyProvider(Repository<Effector> effectorRepository,
                               JsonCodec jsonCodec) {
        this.effectorRepository = effectorRepository;
        this.jsonCodec = jsonCodec;
    }

    private static record InternalSchema(
        String name,
        String description,
        String protocol,
        List<String> effectors) {
    }

    @Override
    public Schema provide(String json) {
        var reference = new AtomicReference<InternalSchema>(this.jsonCodec.unmarshal(json, InternalSchema.class));
        return (Schema) Proxy.newProxyInstance(
            Thread.currentThread().getContextClassLoader(),
            new Class<?>[]{actualTypeArguments(this.getClass())},
            (proxy, method, args) -> {
                var schema = reference.get();
                return switch (method.getName()) {
                    case "toString" -> this.jsonCodec.marshal(schema);
                    case "hashCode" -> System.identityHashCode(proxy);
                    case "equals" -> this.equals(proxy, args);
                    case "effectors" -> {
                        yield schema.effectors().stream()
                            .map(this.effectorRepository::find)
                            .distinct()
                            .toList();
                    }
                    default ->
                        InternalSchema.class.getMethod(method.getName()).invoke(schema);
                };
            }
        );
    }

    private boolean equals(Object proxy, Object[] args) {
        return args != null && args.length == 1 && proxy == args[0];
    }
}
