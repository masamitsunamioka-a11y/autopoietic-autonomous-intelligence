package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.cli;

import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.working.Trace;

import java.time.Instant;

record TraceImpl(String cue, Object content) implements Trace {
    TraceImpl(String cue, Object content) {
        this.cue = cue + "@" + Instant.now();
        this.content = content;
    }
}
