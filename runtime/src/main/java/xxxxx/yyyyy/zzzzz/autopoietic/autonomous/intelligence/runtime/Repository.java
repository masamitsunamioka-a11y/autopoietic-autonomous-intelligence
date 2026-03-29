package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime;

import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.AggregateRoot;

import java.util.List;

public interface Repository<T extends AggregateRoot> {
    T find(String id);

    List<T> findAll();

    boolean exists(String id);
}
