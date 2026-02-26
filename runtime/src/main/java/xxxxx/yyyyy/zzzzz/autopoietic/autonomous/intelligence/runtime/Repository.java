package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime;

import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Engram;

import java.util.List;

public interface Repository<T> {
    T find(String id);

    List<T> findAll();

    void store(Engram engram);

    void remove(String id);
}
