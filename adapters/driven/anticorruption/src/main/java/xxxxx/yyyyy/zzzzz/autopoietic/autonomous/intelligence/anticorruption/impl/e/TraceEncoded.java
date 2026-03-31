package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.e;

import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Event;

public record TraceEncoded(
    String id,
    long occurredOn,
    int version,
    Object content
) implements Event {
}
