package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime;

import java.util.List;

public interface Repository<I, E> {
    I find(String id);

    List<I> findAll();

    void store(E e);

    void remove(String id);
}
