package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.working;

import java.util.List;

public interface Memory {
    void encode(Trace trace);

    Trace retrieve(String cue);

    List<Trace> retrieve();

    void decay();
}
