package xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.runtime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record Upgrade(String newInstructions,
                      @NotNull List<AgentDefinition> newAgents,
                      @NotNull List<TopicDefinition> newTopics,
                      @NotNull List<ActionDefinition> newActions) {
    public Upgrade {
        newAgents = (newAgents == null)
                ? List.of()
                : List.copyOf(newAgents);
        newTopics = (newTopics == null)
                ? List.of()
                : List.copyOf(newTopics);
        newActions = (newActions == null)
                ? List.of()
                : List.copyOf(newActions);
    }

    public record AgentDefinition(@NotBlank String name,
                                  @NotBlank String label,
                                  @NotBlank String description,
                                  @NotBlank String instructions,
                                  @NotNull List<String> topics,
                                  @NotBlank String rawJson) {
        public AgentDefinition {
            topics = (topics == null) ? List.of() : List.copyOf(topics);
        }
    }

    public record TopicDefinition(@NotBlank String name,
                                  @NotBlank String label,
                                  @NotBlank String description,
                                  @NotBlank String instructions,
                                  @NotNull List<ActionDefinition> actions,
                                  @NotBlank String rawJson) {
        public TopicDefinition {
            actions = (actions == null) ? List.of() : List.copyOf(actions);
        }
    }

    public record ActionDefinition(@NotBlank String name) {
    }
}
