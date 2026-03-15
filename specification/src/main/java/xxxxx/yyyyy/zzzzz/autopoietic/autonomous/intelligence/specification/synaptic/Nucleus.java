package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.synaptic;

import java.util.function.Consumer;

public interface Nucleus {
    <T extends Potential>
    void integrate(T potential, Consumer<T> consumer);
}
