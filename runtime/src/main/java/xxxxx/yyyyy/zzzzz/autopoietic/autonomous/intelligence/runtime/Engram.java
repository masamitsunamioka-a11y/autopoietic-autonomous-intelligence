package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime;

import java.util.function.Function;

public interface Engram {
    String name();

    default String transcribe(Function<Engram, String> f) {
        return f.apply(this);
    }
}
