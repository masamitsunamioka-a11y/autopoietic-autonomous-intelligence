package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption;

import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Entity;

import java.util.List;

public interface Adapter<I extends Entity> {
    I fetch(String id);

    List<I> fetchAll();

    void publish(I object);

    void revoke(String id);

    void revokeAll(List<String> ids);
}
