package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.impl.e;

import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.Event;

public record MethodInvoked(
    String type,
    String className,
    String methodName
) implements Event {
    public MethodInvoked(String className, String methodName) {
        this("method-invoked", className, methodName);
    }
}
