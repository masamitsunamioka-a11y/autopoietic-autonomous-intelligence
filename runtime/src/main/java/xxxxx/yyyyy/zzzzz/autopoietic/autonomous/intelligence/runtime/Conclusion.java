package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime;

import jakarta.validation.constraints.NotBlank;

import java.util.Objects;

public record Conclusion(@NotBlank String phase,
                         @NotBlank String thought,
                         String handoffTo,
                         String action,
                         String answer) {
    public Conclusion {
        switch (phase) {
            case "ACT" -> Objects.requireNonNull(action,
                    "Action name is required for " + phase + " phase.");
            case "HANDOFF" -> Objects.requireNonNull(handoffTo,
                    "Agent name is required for " + phase + " phase.");
            case "ANSWER" -> Objects.requireNonNull(answer,
                    "Answer text is required for " + phase + " phase.");
        }
    }
}
