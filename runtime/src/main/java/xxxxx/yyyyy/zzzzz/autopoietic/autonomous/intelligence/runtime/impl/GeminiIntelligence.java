package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.Util;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Intelligence;

import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
public class GeminiIntelligence implements Intelligence {
    private static final Logger logger = LoggerFactory.getLogger(GeminiIntelligence.class);
    private final Gson gson;
    private final GeminiClient geminiClient;
    private final Validator validator;

    public GeminiIntelligence() {
        this.gson = new GsonBuilder().serializeNulls().create();
        this.geminiClient = new GeminiClient();
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            this.validator = validatorFactory.getValidator();
        }
    }

    @Override
    public <T> T reason(String prompt, Class<T> type) {
        String payload = this.geminiClient.post(prompt);
        String cleanedJson = Util.cleanJson(payload);
        T result = this.gson.fromJson(cleanedJson, type);
        Set<ConstraintViolation<T>> violations = this.validator.validate(result);
        if (!violations.isEmpty()) {
            String reasons = violations.stream()
                    .map(x -> String.format("[%s] %s", x.getPropertyPath(), x.getMessage()))
                    .collect(Collectors.joining(", "));
            throw new IllegalStateException("""
                    [Integrity Violation] Intelligence produced a malformed conclusion for %s.
                    Violations: %s
                    """.formatted(type.getSimpleName(), reasons));
        }
        return result;
    }
}
