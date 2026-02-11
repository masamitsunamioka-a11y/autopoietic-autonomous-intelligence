package xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.anticorruption;

public interface Translator<I, E> {
    I toInternal(String identifier, E external);

    E toExternal(String identifier, I internal);
}
