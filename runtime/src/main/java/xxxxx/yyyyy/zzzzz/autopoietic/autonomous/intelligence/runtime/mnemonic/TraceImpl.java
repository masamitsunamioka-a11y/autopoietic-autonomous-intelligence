package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.mnemonic;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.mnemonic.Trace;

import java.time.Instant;

public record TraceImpl(
    @NotBlank String id,
    @NotNull Object content)
    implements Trace {
    public TraceImpl(String id, Object content) {
        this.id = id.contains("@") ? id : id + "@" + Instant.now();
        this.content = content;
    }

    public String prefixOf() {
        var at = this.id.lastIndexOf('@');
        return at >= 0 ? this.id.substring(0, at) : this.id;
    }

    public Instant timestampOf() {
        var at = this.id.lastIndexOf('@');
        return Instant.parse(this.id.substring(at + 1));
    }

    public static TraceImpl unresolvedEffector(String name) {
        return new TraceImpl("[SYSTEM]",
            "[SYSTEM WARNING] Effector '" + name
                + "' does not exist. Choose a valid Effector or VOCALIZE.");
    }

    public static TraceImpl habituatedEffector(String name) {
        return new TraceImpl("[SYSTEM]",
            "[SYSTEM WARNING] Effector " + name
                + " fired 3+ consecutive times. Do not FIRE again.");
    }

    public static TraceImpl unresolvedArea(String name) {
        return new TraceImpl("[SYSTEM]",
            "[SYSTEM WARNING] Area '" + name
                + "' does not exist. Choose a valid Area or VOCALIZE.");
    }

    public static TraceImpl vocalized(String area, String response) {
        return new TraceImpl(area, response);
    }

    public static TraceImpl inhibited(String response) {
        return new TraceImpl("[INHIBITION]", response);
    }

    public static TraceImpl fired(String effector, Object output) {
        return new TraceImpl("[EFFECTOR:" + effector + "]", output);
    }

    public static TraceImpl introspected(String signal) {
        return new TraceImpl("[INTROSPECTION]", signal);
    }
}
