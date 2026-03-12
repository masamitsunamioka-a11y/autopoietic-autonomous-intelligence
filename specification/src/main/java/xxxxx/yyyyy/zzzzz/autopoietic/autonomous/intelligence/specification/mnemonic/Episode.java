package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.mnemonic;

import java.util.Map;

public interface Episode {
    void encode(Trace trace);

    Trace retrieve(String id);

    Map<String, Object> retrieve();

    void decay();
}
