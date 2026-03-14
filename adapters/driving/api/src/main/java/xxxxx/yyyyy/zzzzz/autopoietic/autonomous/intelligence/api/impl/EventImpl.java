package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.impl;

import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.Event;

public record EventImpl(
    String type,
    String location,
    Object content) implements Event {
}
