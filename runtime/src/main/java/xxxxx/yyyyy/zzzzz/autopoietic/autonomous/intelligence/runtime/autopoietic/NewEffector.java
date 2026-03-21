package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.autopoietic;

import jakarta.validation.constraints.NotBlank;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Effector;

import java.util.Map;

public record NewEffector(
    @NotBlank String id,
    @NotBlank String tuning
) implements Effector {
    @Override
    public Map<String, Object>
    fire(Map<String, Object> input) {
        return Map.of();
    }
}
