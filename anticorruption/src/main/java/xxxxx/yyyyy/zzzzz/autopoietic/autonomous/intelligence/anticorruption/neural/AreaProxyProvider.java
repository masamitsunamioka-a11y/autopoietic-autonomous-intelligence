package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.neural;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.JsonCodec;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.ProxyProvider;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.*;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Util.actualTypeArguments;

@ApplicationScoped
public class AreaProxyProvider implements ProxyProvider<Area> {
    private static final Logger logger = LoggerFactory.getLogger(AreaProxyProvider.class);
    private final Repository<Neuron, Engram> neuronRepository;
    private final Repository<Effector, Organ> effectorRepository;
    private final JsonCodec jsonCodec;

    @Inject
    public AreaProxyProvider(Repository<Neuron, Engram> neuronRepository,
                             Repository<Effector, Organ> effectorRepository,
                             JsonCodec jsonCodec) {
        this.neuronRepository = neuronRepository;
        this.effectorRepository = effectorRepository;
        this.jsonCodec = jsonCodec;
    }

    private static record InternalArea(
        String name,
        String tuning,
        List<String> neurons,
        List<String> effectors) {
    }

    @Override
    public Area provide(String json) {
        var reference = new AtomicReference<InternalArea>(this.jsonCodec.unmarshal(json, InternalArea.class));
        return (Area) Proxy.newProxyInstance(
            Thread.currentThread().getContextClassLoader(),
            new Class<?>[]{actualTypeArguments(this.getClass())},
            (proxy, method, args) -> {
                var area = reference.get();
                return switch (method.getName()) {
                    case "toString" -> this.jsonCodec.marshal(area);
                    case "hashCode" -> System.identityHashCode(proxy);
                    case "equals" -> this.equals(proxy, args);
                    case "neurons" -> {
                        yield area.neurons().stream()
                            .map(this.neuronRepository::find)
                            .distinct()
                            .toList();
                    }
                    case "effectors" -> {
                        yield area.effectors().stream()
                            .map(this.effectorRepository::find)
                            .distinct()
                            .toList();
                    }
                    default -> InternalArea.class.getMethod(method.getName()).invoke(area);
                };
            }
        );
    }

    private boolean equals(Object proxy, Object[] args) {
        return args != null && args.length == 1 && proxy == args[0];
    }
}
