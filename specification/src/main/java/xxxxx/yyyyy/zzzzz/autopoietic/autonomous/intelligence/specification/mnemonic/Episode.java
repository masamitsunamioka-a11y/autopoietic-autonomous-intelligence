package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.mnemonic;

import java.util.List;

public interface Episode {
    void encode(Trace trace);

    Trace retrieve(String id);

    List<Trace> retrieve();

    void decay();

    void promote();
}
