package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.e;

import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Event;

import java.util.List;

public record NeuronCreated(
    String id,
    long occurredOn,
    String tuning,
    List<String> effectors
) implements Event {
}
