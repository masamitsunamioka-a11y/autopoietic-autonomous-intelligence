package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural;

import java.util.function.Function;

public interface Engram {
    /// [Engineering] No Kandel equivalent — required for system addressing.
    String name();

    /// [Engineering] Java functional pattern — no Kandel method equivalent.
    default String encode(Function<Engram, String> f) {
        return f.apply(this);
    }
}
