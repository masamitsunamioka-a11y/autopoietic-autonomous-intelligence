package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.synaptic;

public interface Nucleus {
    /// [Engineering] String signal = encoded prompt; Class<T> = output record type.
    <T> T integrate(String signal, Class<T> type);
}
