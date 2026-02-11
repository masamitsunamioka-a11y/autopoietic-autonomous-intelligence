package xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.anticorruption;

import java.util.List;

public interface Adapter<I, E> {
    List<I> toInternal();

    I toInternal(String identifier);

    void toExternal(String identifier, I internal);

    void toExternal(String identifier, String internal);
}
