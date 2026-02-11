package xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.runtime;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.stream.Collectors;

public class Thinker {
    private static final Logger logger = LoggerFactory.getLogger(Thinker.class);
    private final Gson gson;
    private final GeminiClient geminiClient;
    private final Validator validator;

    public Thinker() {
        this.gson = new GsonBuilder().serializeNulls().create();
        this.geminiClient = new GeminiClient();
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            this.validator = validatorFactory.getValidator();
        }
    }

    public <T> T think(String prompt, Class<T> clazz) {
        String payload = this.geminiClient.post(prompt);
        String cleanedJson = Util.cleanJson(payload);
        /// logger.debug("[THINKER_PARSE] Target: {}, CleanedJson: {}", clazz.getSimpleName(), cleanedJson);
        T result = this.gson.fromJson(cleanedJson, clazz);
        Set<ConstraintViolation<T>> violations = this.validator.validate(result);
        if (!violations.isEmpty()) {
            String reasons = violations.stream()
                    .map(x -> String.format("[%s] %s",
                            x.getPropertyPath(),
                            x.getMessage()))
                    .collect(Collectors.joining(", "));
            throw new IllegalStateException("""
                    [Integrity Violation] Thinker produced a malformed conclusion for %s.
                    Violations: %s
                    """.formatted(clazz.getSimpleName(), reasons));
        }
        return result;
    }
}
