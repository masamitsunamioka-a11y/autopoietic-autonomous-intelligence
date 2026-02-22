package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime;

import java.util.List;

public interface Repository<T> {
    T find(String id);

    List<T> findAll();

    void store(String id, Storable storable);

    void remove(String id);
}
