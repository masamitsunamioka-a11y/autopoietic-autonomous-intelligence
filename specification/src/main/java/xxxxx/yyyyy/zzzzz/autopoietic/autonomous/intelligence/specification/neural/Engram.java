package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural;

import java.util.function.Function;

public interface Engram {
    String name();

    default String transcribe(Function<Engram, String> f) {
        return f.apply(this);
    }
}
