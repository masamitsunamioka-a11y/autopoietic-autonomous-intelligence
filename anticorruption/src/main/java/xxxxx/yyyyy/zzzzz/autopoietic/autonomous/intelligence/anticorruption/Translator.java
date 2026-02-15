package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption;

public interface Translator<I, E> {
    default I translateFrom(String id, E source) {
        throw new UnsupportedOperationException();
    }

    default E translateTo(String id, I object) {
        throw new UnsupportedOperationException();
    }
}
