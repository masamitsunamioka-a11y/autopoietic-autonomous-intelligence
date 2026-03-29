package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption;

public interface Translator<I, E extends Resource> {
    I internalize(E object);

    E externalize(I object);
}
