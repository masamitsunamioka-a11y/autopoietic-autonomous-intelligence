package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.e;

import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Event;

public record EffectorCreated(
    String id,
    long occurredOn,
    int version,
    String program
) implements Event {
}
