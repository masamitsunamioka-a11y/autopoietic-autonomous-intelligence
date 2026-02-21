package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.experimental;

import com.google.genai.Client;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption.JsonCodec;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Intelligence;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
        if (logger.isTraceEnabled()) {
            this.dump(type.getSimpleName().toLowerCase(), "request", prompt);
            logger.trace("\n{}", prompt);
        }
        var text = this.generate(prompt);
        if (logger.isTraceEnabled()) {
            this.dump(type.getSimpleName().toLowerCase(), "response", text);
            logger.trace("\n{}", text);
        }
        T result = this.jsonCodec.unmarshal(text, type);
        this.validate(result);
        return result;
    }

    private String generate(String prompt) {
        /// Client instance should also be provided via a CDI Provider.
        try (var client = new Client()) {
            return client.models.generateContent(MODEL, prompt, null).text();
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
            throw new IllegalStateException(
                String.format("[Integrity Violation] %s: %s",
                    result.getClass().getSimpleName(), reasons));
        }
    }

    private void dump(String phase, String mode, String content) {
        try {
            var dump = Paths.get("dump");
            if (Files.notExists(dump)) {
                Files.createDirectories(dump);
            }
            var now = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
            var name = String.format("%s_%s_%s.txt", now, mode, phase);
            Files.writeString(
                dump.resolve(name),
                content,
                StandardCharsets.UTF_8
            );
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
