package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption;

public interface Translator<I, E> {
    I internalize(E object);

    E externalize(I object);
}
