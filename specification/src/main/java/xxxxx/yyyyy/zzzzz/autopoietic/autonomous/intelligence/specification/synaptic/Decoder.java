package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.synaptic;

public interface Decoder {
    <T> T decode(String signal, Class<T> response);
}
