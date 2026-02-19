package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption;

import java.util.List;

public interface Adapter<I, E> {
    I fetch(String id);

    List<I> fetchAll();

    void publish(String id, E source);

    void revoke(String id);
}
