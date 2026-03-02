package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.working;

import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.working.Trace;

import java.time.Instant;

public record TraceImpl(String cue, Object content) implements Trace {
    public TraceImpl(String cue, Object content) {
        this.cue = cue.contains("@") ? cue : cue + "@" + Instant.now();
        this.content = content;
    }
}
