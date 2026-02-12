package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption;

public interface Translator<I, E> {
    I toInternal(String identifier, E external);

    E toExternal(String identifier, I internal);
}
