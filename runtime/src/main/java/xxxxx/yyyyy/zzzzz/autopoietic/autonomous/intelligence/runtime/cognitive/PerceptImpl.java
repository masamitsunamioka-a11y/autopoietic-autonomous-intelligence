package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.cognitive;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.cognitive.Percept;

public record PerceptImpl(
    @NotBlank String content,
    @NotBlank String location,
    @Positive double intensity,
    @Positive long duration
) implements Percept {
}
