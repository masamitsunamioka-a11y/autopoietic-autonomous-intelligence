package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption;

import java.util.List;

public interface Adapter<I, E> {
    I toInternal(String id);

    List<I> toInternal();

    void toExternal(String id, E source);
}
