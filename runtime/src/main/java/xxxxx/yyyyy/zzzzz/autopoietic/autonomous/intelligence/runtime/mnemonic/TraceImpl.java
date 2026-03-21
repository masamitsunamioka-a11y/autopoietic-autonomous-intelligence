package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.mnemonic;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.mnemonic.Trace;

public record TraceImpl(
    @NotBlank String id,
    @NotNull Object content
) implements Trace {
}
