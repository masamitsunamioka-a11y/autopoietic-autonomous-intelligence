package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record Upgrade(@NotBlank String reasoning,
                      @DecimalMin("0.0") @DecimalMax("1.0") double confidence,
                      @NotBlank String newInstructions,
                      @NotNull List<@Valid AgentDefinition> newAgents,
                      @NotNull List<@Valid TopicDefinition> newTopics,
                      @NotNull List<@Valid ActionDefinition> newActions) {
    public record AgentDefinition(@NotBlank String name,
                                  @NotBlank String label,
                                  @NotBlank String description,
                                  @NotBlank String instructions,
                                  @NotNull List<String> topics,
                                  @NotBlank String rawJson) {
    }

    public record TopicDefinition(@NotBlank String name,
                                  @NotBlank String label,
                                  @NotBlank String description,
                                  @NotBlank String instructions,
                                  @NotNull List<String> actions,
                                  @NotBlank String rawJson) {
    }

    public record ActionDefinition(@NotBlank String name,
                                   @NotBlank String label,
                                   @NotBlank String description,
                                   @NotNull List<String> relatedTopics,
                                   @NotBlank String rawJson) {
    }
}
