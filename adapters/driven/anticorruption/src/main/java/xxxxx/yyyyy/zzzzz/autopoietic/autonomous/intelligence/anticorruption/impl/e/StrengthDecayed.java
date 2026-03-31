package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.e;

import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Event;

public record StrengthDecayed(
    String id,
    long occurredOn,
    int version,
    double newStrength
) implements Event {
}
