package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.mnemonic;

import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.mnemonic.Trace;

import java.time.Instant;

public record TraceImpl(String id, Object content) implements Trace {
    /// [Engineering] As detailed in docs/kandel.md
    public TraceImpl(String id, Object content) {
        this.id = id.contains("@") ? id : id + "@" + Instant.now();
        this.content = content;
    }
}
