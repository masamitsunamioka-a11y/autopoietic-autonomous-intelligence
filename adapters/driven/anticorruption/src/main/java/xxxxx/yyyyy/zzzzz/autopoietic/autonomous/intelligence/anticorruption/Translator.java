package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption;

public interface Translator<I, E> {
    I translateFrom(String id, E source);

    E translateTo(String id, I object);
}
