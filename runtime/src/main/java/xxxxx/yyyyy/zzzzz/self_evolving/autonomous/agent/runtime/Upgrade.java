package xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.runtime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record Upgrade(String newInstructions,
                      @NotNull List<AgentDefinition> newAgents,
                      @NotNull List<TopicDefinition> newTopics) {
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
