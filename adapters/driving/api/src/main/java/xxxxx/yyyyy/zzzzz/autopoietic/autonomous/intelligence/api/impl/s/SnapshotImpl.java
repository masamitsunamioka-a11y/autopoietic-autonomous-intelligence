package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.impl.s;

import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.Snapshot;

public record SnapshotImpl(
    String name,
    String mimeType,
    Object content,
    long timestamp
) implements Snapshot {
}
