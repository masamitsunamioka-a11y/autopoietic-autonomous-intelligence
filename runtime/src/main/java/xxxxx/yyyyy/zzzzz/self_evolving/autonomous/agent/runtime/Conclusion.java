package xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.runtime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

public record Conclusion(@NotNull InferencePhase phase,
                         @NotBlank String thought,
                         String handoffTo,
                         String action,
                         String answer) {
    public Conclusion {
        switch (phase) {
            case ACT -> Objects.requireNonNull(action,
                    "Action label is required for ACT phase.");
            case HANDOFF -> Objects.requireNonNull(handoffTo,
                    "Target agent name is required for HANDOFF phase.");
            case EVOLVE, ANSWER -> Objects.requireNonNull(answer,
                    "Answer/Requirement text is required for " + phase + " phase.");
        }
    }
}
