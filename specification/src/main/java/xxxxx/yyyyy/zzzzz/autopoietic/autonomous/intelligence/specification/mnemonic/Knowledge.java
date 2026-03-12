package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.mnemonic;

import java.util.Map;

public interface Knowledge {
    void encode(Trace trace);

    Trace retrieve(String id);

    Map<String, Object> retrieve();

    void promote();

    void decay();
}
