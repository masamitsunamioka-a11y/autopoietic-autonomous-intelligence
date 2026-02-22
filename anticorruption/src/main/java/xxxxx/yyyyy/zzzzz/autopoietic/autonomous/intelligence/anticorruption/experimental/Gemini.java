package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.experimental;

import com.google.genai.Client;
import com.google.gson.stream.MalformedJsonException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.JsonCodec;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Intelligence;

import static java.util.stream.Collectors.joining;

/// https://github.com/googleapis/java-genai/blob/main/examples/src/main/java/com/google/genai/examples/GenerateContent.java
@ApplicationScoped
public class Gemini implements Intelligence {
    private static final Logger logger = LoggerFactory.getLogger(Gemini.class);
    private final JsonCodec jsonCodec;
    private final Validator validator;
    private static final String MODEL = "gemini-2.0-flash";

    public Gemini(JsonCodec jsonCodec) {
        this.jsonCodec = jsonCodec;
        try (var validatorFactory = Validation.buildDefaultValidatorFactory()) {
            this.validator = validatorFactory.getValidator();
        }
    }

    @Override
    public <T> T reason(String prompt, Class<T> type) {
        return this.reasonWithRetry(prompt, type, 0);
    }

    private <T> T reasonWithRetry(String prompt, Class<T> type, int retry) {
        if (logger.isTraceEnabled()) {
            logger.trace("\n{}", prompt);
        }
        var text = this.generate(prompt);
        if (logger.isTraceEnabled()) {
            logger.trace("\n{}", text);
        }
        try {
            T result = this.jsonCodec.unmarshal(text, type);
            this.validate(result);
            return result;
        } catch (Exception e) {
            if (this.isMalformed(e) && retry < 2) {
                return this.reasonWithRetry(prompt, type, ++retry);
            }
            throw e;
        }
    }

    private boolean isMalformed(Throwable t) {
        if (t == null) {
            return false;
        }
        if (t instanceof MalformedJsonException) {
            return true;
        }
        return this.isMalformed(t.getCause());
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
