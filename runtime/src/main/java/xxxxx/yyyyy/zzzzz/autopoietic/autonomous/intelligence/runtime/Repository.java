package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime;

import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.AggregateRoot;

import java.util.List;

public interface Repository<T extends AggregateRoot> {
    T find(String id);

    List<T> findAll();

    void store(T object);

    void remove(String id);

    void removeAll(List<String> ids);

    boolean exists(String id);
}
