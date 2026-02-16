package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime;

import java.util.List;

public interface Repository<T> {
    default T find(String id) {
        throw new UnsupportedOperationException();
    }

    default List<T> findAll() {
        throw new UnsupportedOperationException();
    }

    default void store(String id, String source) {
        throw new UnsupportedOperationException();
    }

    default void store(String id, T source) {
        throw new UnsupportedOperationException();
    }

    default void remove(String id) {
        throw new UnsupportedOperationException();
    }
}
