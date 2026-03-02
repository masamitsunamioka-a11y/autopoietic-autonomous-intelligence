package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime;

import java.util.List;
import java.util.Objects;

public interface Repository<I, E> {
    I find(String id);

    List<I> findAll();

    void store(E e);

    void remove(String id);

    default void removeAll(List<String> ids) {
        throw new UnsupportedOperationException();
    }

    default boolean exists(String id) {
        return Objects.nonNull(this.find(id));
    }
}
