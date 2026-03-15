package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.impl;

import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.Event;

public record TraceEvent(
    String type,
    String className,
    String methodName) implements Event {
    public TraceEvent(String className, String methodName) {
        this("trace", className, methodName);
    }
}
