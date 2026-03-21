package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.impl.transmission;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.Validation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.cognitive.Decision;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.networking.Fluctuation;

import static java.util.Objects.requireNonNull;

@ApplicationScoped
public class ValidatorImpl implements Validator {
    private static final Logger logger = LoggerFactory.getLogger(ValidatorImpl.class);
    private final jakarta.validation.Validator validator;

    public ValidatorImpl() {
        try (var validatorFactory =
                 Validation.buildDefaultValidatorFactory()) {
            this.validator = validatorFactory.getValidator();
        }
    }

    @Override
    public <T> void validate(T result) {
        var violations = this.validator.validate(result);
        if (!violations.isEmpty()) {
            throw new IllegalStateException();
        }
        this.validateConstraints(result);
    }

    private <T> void validateConstraints(T result) {
        switch (result) {
            case Decision x -> this.validateDecision(x);
            case Fluctuation x -> this.validateFluctuation(x);
            default -> {
            }
        }
    }

    private void validateDecision(Decision decision) {
        switch (decision.process().toUpperCase()) {
            case "VOCALIZE", "INHIBIT" -> requireNonNull(decision.response(), "response required");
            case "FIRE" -> requireNonNull(decision.effector(), "effector required");
            case "PROJECT" -> requireNonNull(decision.area(), "area required");
            default -> {
            }
        }
    }

    private void validateFluctuation(Fluctuation fluctuation) {
        if (fluctuation.aroused()) {
            requireNonNull(fluctuation.signal(), "signal required");
        }
    }
}
