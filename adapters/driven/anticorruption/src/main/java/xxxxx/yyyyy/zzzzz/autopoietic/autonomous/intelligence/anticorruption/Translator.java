package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption;

public interface Translator<I, E> {
    I translateFrom(String id, String source);

    String translateTo(String id, E object);
}
