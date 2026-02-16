package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Intelligence;

import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
public class PureJavaIntelligence implements Intelligence {
    private static final Logger logger = LoggerFactory.getLogger(PureJavaIntelligence.class);
    private final ExternalService externalService;
    private final Validator validator;

    public PureJavaIntelligence(ExternalService externalService) {
        this.externalService = externalService;
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            this.validator = validatorFactory.getValidator();
        }
    }

    @Override
    public <T> T reason(String prompt, Class<T> type) {
        T result = this.externalService.call(prompt, type);
        Set<ConstraintViolation<T>> violations = this.validator.validate(result);
        if (!violations.isEmpty()) {
            String reasons = violations.stream()
                    .map(x -> String.format("[%s] %s",
                            x.getPropertyPath(),
                            x.getMessage()))
                    .collect(Collectors.joining(", "));
            throw new IllegalStateException("""
                    [Integrity Violation] %s: %s"
                    """.formatted(type.getSimpleName(), reasons));
        }
        return result;
    }
}
