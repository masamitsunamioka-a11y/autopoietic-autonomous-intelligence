package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.ProxyProvider;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Serializer;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Neuron;

import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicReference;

import static xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.Utility.actualTypeArguments;

@ApplicationScoped
public class NeuronProxyProvider implements ProxyProvider<Neuron> {
    private static final Logger logger = LoggerFactory.getLogger(NeuronProxyProvider.class);
    private final Serializer serializer;

    @Inject
    public NeuronProxyProvider(Serializer serializer) {
        this.serializer = serializer;
    }

    private static record InternalNeuron(
        String name,
        String tuning) {
    }

    @Override
    public Neuron provide(String json) {
        var reference = new AtomicReference<InternalNeuron>(
            this.serializer.deserialize(json, InternalNeuron.class));
        return (Neuron) Proxy.newProxyInstance(
            Thread.currentThread().getContextClassLoader(),
            new Class<?>[]{actualTypeArguments(this.getClass())},
            (proxy, method, args) -> {
                var neuron = reference.get();
                return switch (method.getName()) {
                    case "toString" -> this.serializer.serialize(neuron);
                    case "hashCode" -> System.identityHashCode(proxy);
                    case "equals" -> this.equals(proxy, args);
                    default -> {
                        yield InternalNeuron.class
                            .getMethod(method.getName())
                            .invoke(neuron);
                    }
                };
            }
        );
    }

    private boolean equals(Object proxy, Object[] args) {
        return args != null && args.length == 1 && proxy == args[0];
    }
}
