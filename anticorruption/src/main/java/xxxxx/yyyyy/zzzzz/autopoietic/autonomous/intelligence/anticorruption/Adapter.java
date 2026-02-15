package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption;

import java.util.List;

public interface Adapter<I, E> {
    default I fetch(String id) {
        throw new UnsupportedOperationException();
    }

    default List<I> fetchAll() {
        throw new UnsupportedOperationException();
    }

    default void publish(String id, E source) {
        throw new UnsupportedOperationException();
    }

    default void revoke(String id) {
        throw new UnsupportedOperationException();
    }
}
