package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.JsonCodec;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.ProxyProvider;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Effector;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Receptor;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.Util.actualTypeArguments;

@ApplicationScoped
public class ReceptorProxyProvider implements ProxyProvider<Receptor> {
    private static final Logger logger = LoggerFactory.getLogger(ReceptorProxyProvider.class);
    private final Repository<Effector> effectorRepository;
    private final JsonCodec jsonCodec;

    @Inject
    public ReceptorProxyProvider(Repository<Effector> effectorRepository,
                                 JsonCodec jsonCodec) {
        this.effectorRepository = effectorRepository;
        this.jsonCodec = jsonCodec;
    }

    private static record InternalReceptor(
        String name,
        String label,
        String description,
        String instructions,
        List<String> effectors) {
    }

    @Override
    public Receptor provide(String json) {
        var reference = new AtomicReference<InternalReceptor>(this.jsonCodec.unmarshal(json, InternalReceptor.class));
        return (Receptor) Proxy.newProxyInstance(
            Thread.currentThread().getContextClassLoader(),
            new Class<?>[]{actualTypeArguments(this.getClass())},
            (proxy, method, args) -> {
                var receptor = reference.get();
                return switch (method.getName()) {
                    case "toString" -> this.jsonCodec.marshal(receptor);
                    case "hashCode" -> System.identityHashCode(proxy);
                    case "equals" -> this.equals(proxy, args);
                    case "effectors" -> {
                        yield receptor.effectors().stream()
                            .map(this.effectorRepository::find)
                            .distinct()
                            .toList();
                    }
                    default -> InternalReceptor.class.getMethod(method.getName()).invoke(receptor);
                };
            }
        );
    }

    private boolean equals(Object proxy, Object[] args) {
        return args != null && args.length == 1 && proxy == args[0];
    }
}
