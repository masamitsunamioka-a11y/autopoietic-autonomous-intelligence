package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural;

import java.util.function.Function;

/// Engravable — a neural structure capable of being modified by experience (Kandel Ch.63).
/// Areas and Neurons are engravable: their tuning changes through synaptic plasticity.
public interface Engravable {
    /// [Engineering] No Kandel equivalent — required for system addressing.
    String name();

    /// [Engineering] Java functional pattern — no Kandel method equivalent.
    default String encode(Function<Engravable, String> f) {
        return f.apply(this);
    }
}
