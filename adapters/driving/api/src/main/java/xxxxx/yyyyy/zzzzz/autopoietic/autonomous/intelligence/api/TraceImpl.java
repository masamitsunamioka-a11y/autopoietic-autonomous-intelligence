package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api;

import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.mnemonic.Trace;

import java.time.Instant;

record TraceImpl(String id, Object content) implements Trace {
    TraceImpl(String id, Object content) {
        this.id = id.contains("@") ? id : id + "@" + Instant.now();
        this.content = content;
    }
}
