package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.JsonCodec;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.ProxyProvider;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Repository;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Effector;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Module;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.Util.actualTypeArguments;

@ApplicationScoped
public class ModuleProxyProvider implements ProxyProvider<Module> {
    private static final Logger logger = LoggerFactory.getLogger(ModuleProxyProvider.class);
    private final Repository<Effector> effectorRepository;
    private final JsonCodec jsonCodec;

    @Inject
    public ModuleProxyProvider(Repository<Effector> effectorRepository,
                               JsonCodec jsonCodec) {
        this.effectorRepository = effectorRepository;
        this.jsonCodec = jsonCodec;
    }

    private static record InternalModule(
        String name,
        String function,
        String disposition,
        List<String> effectors) {
    }

    @Override
    public Module provide(String json) {
        var reference = new AtomicReference<InternalModule>(this.jsonCodec.unmarshal(json, InternalModule.class));
        return (Module) Proxy.newProxyInstance(
            Thread.currentThread().getContextClassLoader(),
            new Class<?>[]{actualTypeArguments(this.getClass())},
            (proxy, method, args) -> {
                var module = reference.get();
                return switch (method.getName()) {
                    case "toString" -> this.jsonCodec.marshal(module);
                    case "hashCode" -> System.identityHashCode(proxy);
                    case "equals" -> this.equals(proxy, args);
                    case "effectors" -> {
                        yield module.effectors().stream()
                            .map(this.effectorRepository::find)
                            .distinct()
                            .toList();
                    }
                    default -> InternalModule.class.getMethod(method.getName()).invoke(module);
                };
            }
        );
    }

    private boolean equals(Object proxy, Object[] args) {
        return args != null && args.length == 1 && proxy == args[0];
    }
}
