package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.impl.e;

import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.Event;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.Snapshot;

import java.util.List;

public record FileSystemChanged(
    String type,
    List<Snapshot> content
) implements Event {
    public FileSystemChanged(List<Snapshot> content) {
        this("filesystem-changed", content);
    }
}
