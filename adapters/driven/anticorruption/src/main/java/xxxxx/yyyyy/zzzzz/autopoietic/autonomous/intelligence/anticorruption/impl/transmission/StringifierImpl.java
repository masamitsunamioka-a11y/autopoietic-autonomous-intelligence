package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.transmission;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Serializer;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.mnemonic.Trace;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Area;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Effector;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Neuron;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class StringifierImpl implements Stringifier {
    private static final Logger logger = LoggerFactory.getLogger(StringifierImpl.class);
    private final Serializer serializer;

    @Inject
    public StringifierImpl(Serializer serializer) {
        this.serializer = serializer;
    }

    @Override
    public String stringify(Object value, boolean detailed) {
        return switch (value) {
            case String x -> x;
            case Area x -> this.stringify(x, detailed);
            case Neuron x -> this.stringify(x);
            case Effector x -> this.stringify(x);
            case Trace x -> this.stringify(x);
            case Map<?, ?> x -> this.stringify(x);
            case List<?> x -> this.stringify(x, detailed);
            default -> throw new IllegalArgumentException();
        };
    }

    private String stringify(Area area, boolean detailed) {
        return detailed
            ? this.serializer.serialize(Map.of(
            "id", area.id(),
            "tuning", area.tuning(),
            "neurons", (Object) area.neurons(),
            "effectors", area.effectors()))
            : this.serializer.serialize(Map.of(
            "id", area.id(),
            "tuning", area.tuning()));
    }

    private String stringify(Neuron neuron) {
        return this.serializer.serialize(Map.of(
            "id", neuron.id(),
            "tuning", neuron.tuning()));
    }

    private String stringify(Effector effector) {
        return this.serializer.serialize(Map.of(
            "id", effector.id(),
            "tuning", effector.tuning()));
    }

    private String stringify(Trace trace) {
        return this.serializer.serialize(Map.of(
            trace.id(), trace.content()));
    }

    private String stringify(Map<?, ?> map) {
        return this.serializer.serialize(map);
    }

    private String stringify(List<?> list, boolean detailed) {
        if (list.isEmpty()) {
            return "None";
        }
        if (list.getFirst() instanceof Trace) {
            return this.serializer.serialize(
                list.stream()
                    .map(x -> (Trace) x)
                    .collect(Collectors.toMap(
                        x -> this.prefixOf(x.id()),
                        x -> x.content() != null ? x.content() : "",
                        (x, y) -> y,
                        LinkedHashMap::new)));
        }
        return this.serializer.serialize(list.stream()
            .map(x -> this.stringify(x, detailed))
            .toList());
    }

    private String prefixOf(String id) {
        var at = id.lastIndexOf('@');
        return at >= 0 ? id.substring(0, at) : id;
    }
}
