package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption;

import com.google.genai.Client;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Nucleus;

import static java.util.stream.Collectors.joining;

/// https://github.com/googleapis/java-genai/blob/main/examples/src/main/java/com/google/genai/examples/GenerateContent.java
@ApplicationScoped
public class NucleusImpl implements Nucleus {
    private static final Logger logger = LoggerFactory.getLogger(NucleusImpl.class);
    private final JsonCodec jsonCodec;
    private final Validator validator;
    private static final String MODEL = "gemini-2.0-flash";

    public NucleusImpl(JsonCodec jsonCodec) {
        this.jsonCodec = jsonCodec;
        try (var validatorFactory = Validation.buildDefaultValidatorFactory()) {
            this.validator = validatorFactory.getValidator();
        }
    }

    @Override
    public <T> T compute(String encoding, Class<T> type) {
        if (logger.isTraceEnabled()) {
            logger.trace("\n{}", encoding);
        }
        var text = this.generate(encoding);
        if (logger.isTraceEnabled()) {
            logger.trace("\n{}", text);
        }
        T result = this.jsonCodec.unmarshal(text, type);
        this.validate(result);
        return result;
    }

    private String generate(String prompt) {
        /// Client instance should also be provided via a CDI Provider.
        try (var client = new Client()) {
            return client.models
                .generateContent(MODEL, prompt, null).text();
        }
    }

    private <T> void validate(T result) {
        var violations = this.validator.validate(result);
        if (!violations.isEmpty()) {
            var reasons = violations.stream()
                .map(x -> String.format("[%s] %s",
                    x.getPropertyPath(),
                    x.getMessage()))
                .collect(joining(", "));
            throw new IllegalStateException(reasons);
        }
    }
}
