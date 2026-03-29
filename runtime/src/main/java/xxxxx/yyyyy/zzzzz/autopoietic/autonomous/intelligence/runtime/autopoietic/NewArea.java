package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.autopoietic;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Area;

import java.util.List;

public record NewArea(
    @NotBlank String id,
    @NotBlank String tuning,
    @NotNull List<String> neurons
) implements Area {
}
