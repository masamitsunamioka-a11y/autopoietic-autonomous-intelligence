package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.e;

import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.Event;

import java.util.List;

public record AreaCreated(
    String id,
    long occurredOn,
    int version,
    String tuning,
    List<String> neurons
) implements Event {
}
