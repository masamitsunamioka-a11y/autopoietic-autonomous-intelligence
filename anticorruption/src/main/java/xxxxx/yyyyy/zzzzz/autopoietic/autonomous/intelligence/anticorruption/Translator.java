package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption;

public interface Translator<I, E> {
    I toInternal(String id, E source);

    E toExternal(String id, I object);
}
