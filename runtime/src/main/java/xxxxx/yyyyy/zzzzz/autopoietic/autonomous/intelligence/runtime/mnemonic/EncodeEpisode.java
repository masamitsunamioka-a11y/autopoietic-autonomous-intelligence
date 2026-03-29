package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.mnemonic;

import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Command;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.mnemonic.Trace;

public record EncodeEpisode(
    Trace payload
) implements Command {
}
