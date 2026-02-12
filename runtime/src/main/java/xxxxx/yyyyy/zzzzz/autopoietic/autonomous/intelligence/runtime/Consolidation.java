package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record Consolidation(@NotEmpty List<@Valid ConsolidatedAgent> consolidatedAgents,
                            @NotEmpty List<@Valid ConsolidatedTopic> consolidatedTopics) {
    public record ConsolidatedAgent(@NotEmpty List<String> consolidants,
                                    @NotBlank String reasoning,
                                    @NotNull @Valid AgentDefinition consolidated) {
    }

    public record ConsolidatedTopic(@NotEmpty List<String> consolidants,
                                    @NotBlank String reasoning,
                                    @NotNull @Valid TopicDefinition consolidated) {
    }

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
}
