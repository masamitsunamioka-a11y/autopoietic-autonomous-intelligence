package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

public record Direction(@NotBlank String agent,
                        @DecimalMin("0.0") @DecimalMax("1.0") double confidence,
                        @NotBlank String reasoning) {
}
