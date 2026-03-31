package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.e;

import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Event;

public record NeuronRemoved(
    String id,
    long occurredOn,
    int version
) implements Event {
}
